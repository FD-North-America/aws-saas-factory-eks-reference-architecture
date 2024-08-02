package com.amazonaws.saas.eks.generator.cashdrawercheckout;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.dto.requests.CashDrawerCheckoutReportRequest;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.*;
import com.amazonaws.saas.eks.exception.InvalidReportRequestException;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.Transaction;
import com.amazonaws.saas.eks.order.model.enums.PaymentType;
import com.amazonaws.saas.eks.order.model.enums.TransactionType;
import com.amazonaws.saas.eks.repository.CashDrawerCheckoutRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amazonaws.saas.eks.util.PDFUtils.*;
import static com.amazonaws.saas.eks.util.Utils.roundValue;

@Service
public class CashDrawerCheckoutReportGenerator
        implements Generator<CashDrawerCheckoutReportRequest, CashDrawerCheckoutReportResponse> {

    private static final Logger logger = LogManager.getLogger(CashDrawerCheckoutReportGenerator.class);

    @Autowired
    private CashDrawerCheckoutRepository reportRepository;

    @Override
    public CashDrawerCheckoutReportResponse generate(String tenantId, CashDrawerCheckoutReportRequest request) {
        logger.info("Generating report {}", ReportName.CASH_DRAWER_CHECKOUT);

        CashDrawerCheckoutReportResponse response = new CashDrawerCheckoutReportResponse();

        CashDrawerDetail cashDrawerDetail = new CashDrawerDetail();

        CashDrawer cashDrawer = reportRepository.getCashDrawerByNumber(tenantId, request.getCashDrawerNumber());
        cashDrawerDetail.setId(cashDrawer.getId());
        cashDrawerDetail.setNumber(cashDrawer.getNumber());
        cashDrawerDetail.setStatus(cashDrawer.getStatus());

        CashDrawerCheckout cashDrawerCheckout = reportRepository.getCashDrawerCheckout(tenantId, cashDrawer.getId(),
                request.getCheckoutDate());
        if (cashDrawerCheckout == null) {
            String errMsg = String.format("The cash drawer %s was not checked out during the date %s",
                    cashDrawer.getId(), request.getCheckoutDate());
            logger.error(errMsg);
            throw new InvalidReportRequestException(errMsg, ReportName.CASH_DRAWER_CHECKOUT.toString(), tenantId);
        }
        cashDrawerDetail.setStartup(cashDrawerCheckout.getStartupDate());
        cashDrawerDetail.setCheckout(cashDrawerCheckout.getCreated());
        cashDrawerDetail.setCleared(cashDrawerCheckout.getClearedDate());

        response.setCashDrawer(cashDrawerDetail);

        List<Order> orders = reportRepository.getOrdersByCashDrawerId(tenantId, cashDrawer.getId(),
                cashDrawerCheckout.getStartupDate(), cashDrawerCheckout.getCreated());

        TransactionRecap txnRecap = getTransactionRecap(orders);
        response.setTransactionRecap(txnRecap);
        response.setCashDrawerTransactions(getDrawerTransactions(orders));
        response.setCashOutCount(getCashOutCount(cashDrawerCheckout, txnRecap));

        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, CashDrawerCheckoutReportRequest request) throws DocumentException  {
        CashDrawerCheckoutReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        addCashDrawerTable(document, data);

        addHeaderText(document, "Transaction Recap");

        addTrxRecapTable(document, data);

        addCreditCardDetailTable(document, data);

        addHeaderText(document, "Cash Out Count");

        addCashOutCountTable(document, data);

        addHeaderText(document, "Drawer Detail Transactions");

        addCashDrawerTrxTable(document, data);

        document.close();
        return outputStream.toByteArray();
    }

    private TransactionRecap getTransactionRecap(List<Order> orders) {
        TransactionRecap txnRecap = new TransactionRecap();
        TransactionRecapAmounts cashAmounts = txnRecap.getCash();
        TransactionRecapAmounts cardAmounts = txnRecap.getCard();
        TransactionRecapAmounts totalAmounts = txnRecap.getTotal();
        CardTotals cardTotals = txnRecap.getCardTotals();
        Map<String, BigDecimal> totalsByCard = cardTotals.getTotalByCard();
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                if (StringUtils.hasLength(t.getPaymentType())
                        && t.getPaymentType().equals(PaymentType.CASH.toString())) {
                    updateCashAmounts(t, cashAmounts);
                } else if (StringUtils.hasLength(t.getPaymentType())
                        && t.getPaymentType().equals(PaymentType.CARD.toString())) {
                    updateCardAmounts(t, cardAmounts, totalsByCard);
                }
            }
        }
        cashAmounts.setBalance(cashAmounts.getSales().add(cashAmounts.getPaidOut()));
        cardAmounts.setBalance(cardAmounts.getSales().add(cardAmounts.getPaidOut()));

        totalAmounts.setSales(cashAmounts.getSales().add(cardAmounts.getSales()));
        totalAmounts.setPaidOut(cashAmounts.getPaidOut());
        totalAmounts.setBalance(cashAmounts.getBalance().add(cardAmounts.getBalance()));

        BigDecimal total = totalsByCard.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        cardTotals.setTotal(total);

        return txnRecap;
    }

    private void updateCashAmounts(Transaction t, TransactionRecapAmounts cashAmounts) {
        if (t.getType().equals(TransactionType.PAID_OUT.toString())) {
            cashAmounts.setPaidOut(cashAmounts.getPaidOut().add(t.getAmount()));
        } else if (t.getType().equals(TransactionType.TENDERED.toString())) {
            cashAmounts.setSales(cashAmounts.getSales().add(t.getAmount()));
        } else if (t.getType().equals(TransactionType.CHANGE.toString())) {
            cashAmounts.setSales(cashAmounts.getSales().subtract(t.getAmount()));
        }
    }

    private void updateCardAmounts(Transaction t, TransactionRecapAmounts cardAmounts,
                                   Map<String, BigDecimal> totalsByCard) {
        cardAmounts.setSales(cardAmounts.getSales().add(t.getAmount()));
        if (totalsByCard.containsKey(t.getCcType())) {
            totalsByCard.put(t.getCcType(), totalsByCard.get(t.getCcType()).add(t.getAmount()));
        } else {
            totalsByCard.put(t.getCcType(), t.getAmount());
        }
    }

    private CashDrawerTransactions getDrawerTransactions(List<Order> orders) {
        CashDrawerTransactions cashDrawerTransactions = new CashDrawerTransactions();
        List<CashDrawerTransactionItem> transactionItems = new ArrayList<>();
        BigDecimal cashSubTotal = BigDecimal.ZERO;
        for (Order o : orders) {
            for (Transaction t : o.getTransactions()) {
                CashDrawerTransactionItem item = new CashDrawerTransactionItem();
                item.setTransactionType(t.getType());
                item.setInvoiceNumber(o.getNumber());
                item.setTenderType(t.getPaymentType());
                item.setTenderNumber(t.getRetRef());
                item.setAmount(roundValue(t.getAmount()));
                transactionItems.add(item);
                cashSubTotal = t.getType().equals(TransactionType.CHANGE.toString())
                    ? cashSubTotal.subtract(t.getAmount())
                    : cashSubTotal.add(t.getAmount());
            }
        }
        cashDrawerTransactions.setCashSubtotal(cashSubTotal);
        cashDrawerTransactions.setItems(transactionItems);
        return cashDrawerTransactions;
    }

    private CashOutCount getCashOutCount(CashDrawerCheckout checkout, TransactionRecap recap) {
        CashOutCount cashOutCount = new CashOutCount();
        BigDecimal cashCounted = checkout.getCashTotalAmount() == null ? BigDecimal.ZERO : checkout.getCashTotalAmount();
        BigDecimal cardCounted = checkout.getCardTotalAmount() == null ? BigDecimal.ZERO : checkout.getCardTotalAmount();

        // Cash row
        cashOutCount.setCashCounted(cashCounted);
        cashOutCount.setCashCalculated(recap.getCash().getSales());

        // Startup row
        cashOutCount.setStartUpAmountCounted(checkout.getStartUpAmount().negate());

        // Paid Out row
        cashOutCount.setPaidOutCalculated(recap.getCash().getPaidOut());

        // Cash Subtotal row
        BigDecimal subTotalCounted = cashCounted.subtract(checkout.getStartUpAmount());
        cashOutCount.setCashSubtotalCounted(subTotalCounted);
        cashOutCount.setCashSubtotalCalculated(recap.getCash().getBalance());
        cashOutCount.setCashSubtotalShort(recap.getCash().getBalance().subtract(subTotalCounted).negate());

        // Credit Card row
        cashOutCount.setCcCounted(cardCounted);
        cashOutCount.setCcCalculated(recap.getCard().getBalance());
        cashOutCount.setCcShort(recap.getCard().getBalance().subtract(cardCounted).negate());

        // Totals row
        cashOutCount.setTotalCounted(cashOutCount.getCashSubtotalCounted().add(cashOutCount.getCcCounted()));
        cashOutCount.setTotalCalculated(cashOutCount.getCashSubtotalCalculated().add(cashOutCount.getCcCalculated()));
        cashOutCount.setTotalShort(cashOutCount.getCashSubtotalShort().add(cashOutCount.getCcShort()));

        cashOutCount.setCcCalculated(recap.getCard().getSales());
        return cashOutCount;
    }

    private void addCashDrawerTable(Document document, CashDrawerCheckoutReportResponse data) throws DocumentException {
        PdfPTable cashDrawerTable = new PdfPTable(5);
        addCashDrawerTableHeader(cashDrawerTable);
        addCashDrawerTableRows(cashDrawerTable, data);
        addTable(document, cashDrawerTable);
    }

    private void addCashDrawerTableHeader(PdfPTable table) {
        final String[] cashDrawerTableHeader = { "Cash Drawer", "Status", "Startup", "Check Out", "Cleared" };
        for (String columnTitle : cashDrawerTableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }
    }

    private void addCashDrawerTableRows(PdfPTable table, CashDrawerCheckoutReportResponse data) {
        CashDrawerDetail cdd = data.getCashDrawer();
        table.addCell(cdd.getNumber());
        addTableCellACenter(table, cdd.getStatus());
        addTableCellACenter(table, String.valueOf(cdd.getStartup()));
        addTableCellACenter(table, String.valueOf(cdd.getCheckout()));
        addTableCellACenter(table, cdd.getCleared() != null ? String.valueOf(cdd.getCleared()) : "-");
    }

    private void addTrxRecapTable(Document document, CashDrawerCheckoutReportResponse data) throws DocumentException {
        final String[] tableHeader = { "", "Sales", "Paid Out", "Balance" };
        PdfPTable table = new PdfPTable(4);

        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }

        addTableHeaderCell(table, "Cash");
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCash().getSales())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCash().getPaidOut())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCash().getBalance())));

        addTableHeaderCell(table, "Credit Card");
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCard().getSales())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCard().getPaidOut())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCard().getBalance())));

        addTableHeaderCell(table, "Totals");
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getTotal().getSales())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getTotal().getPaidOut())));
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getTotal().getBalance())));

        addTable(document, table);
    }

    private void addCreditCardDetailTable(Document document, CashDrawerCheckoutReportResponse data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);

        addTableHeaderCell(table, "Credit Card Detail");
        addTableHeaderCell(table, "");

        for (Map.Entry<String, BigDecimal> e: data.getTransactionRecap().getCardTotals().getTotalByCard().entrySet()) {
            addTableHeaderCell(table, e.getKey());
            addTableCellARight(table, String.valueOf(roundValue(e.getValue())));
        }
        addTableHeaderCell(table, "Totals");
        addTableCellARight(table, String.valueOf(roundValue(data.getTransactionRecap().getCardTotals().getTotal())));

        addTable(document, table);
    }

    private void addCashDrawerTrxTable(Document document, CashDrawerCheckoutReportResponse data) throws DocumentException {
        final String[] tableHeader = { "Tax Type", "Invoice #", "Tender Type", "Tender #", "Amount" };
        PdfPTable table = new PdfPTable(5);

        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }

        for (CashDrawerTransactionItem item : data.getCashDrawerTransactions().getItems()) {
            addTableHeaderCell(table, item.getTransactionType());
            addTableCellACenter(table, item.getInvoiceNumber());
            addTableCellACenter(table, item.getTenderType());
            addTableCellACenter(table, item.getTenderNumber());
            addTableCellARight(table, String.valueOf(roundValue(item.getAmount())));
        }

        addTableHeaderCell(table, "");
        addTableHeaderCell(table, "");
        addTableHeaderCell(table, "");
        addTableHeaderCell(table, "Cash Subtotal");
        addTableCellARight(table, String.valueOf(roundValue(data.getCashDrawerTransactions().getCashSubtotal())));

        addTable(document, table);
    }

    private void addCashOutCountTable(Document document, CashDrawerCheckoutReportResponse data) throws DocumentException {
        final String[] tableHeader = { "", "Counted", "Calculated", "Over-Short"};
        PdfPTable table = new PdfPTable(4);

        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }

        CashOutCount cashOutCount = data.getCashOutCount();

        // Cash Row
        addTableHeaderCell(table, "Cash");
        addTableCellACenter(table, String.valueOf(cashOutCount.getCashCounted()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getCashCalculated()));
        table.addCell("");

        // Start up Row
        addTableHeaderCell(table, "Start Up Amount");
        addTableCellACenter(table, String.valueOf(cashOutCount.getStartUpAmountCounted()));
        table.addCell("");
        table.addCell("");

        // Paid Out row
        addTableHeaderCell(table, "Paid Out");
        table.addCell("");
        addTableCellACenter(table, String.valueOf(cashOutCount.getPaidOutCalculated()));
        table.addCell("");

        // Cash Subtotal row
        addTableHeaderCell(table, "Cash Subtotal");
        addTableCellACenter(table, String.valueOf(cashOutCount.getCashSubtotalCounted()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getCashSubtotalCalculated()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getCashSubtotalShort()));

        // Credit Card row
        addTableHeaderCell(table, "Credit Cards");
        addTableCellACenter(table, String.valueOf(cashOutCount.getCcCounted()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getCcCalculated()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getCcShort()));

        // Total Row
        addTableHeaderCell(table, "Total");
        addTableCellACenter(table, String.valueOf(cashOutCount.getTotalCounted()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getTotalCalculated()));
        addTableCellACenter(table, String.valueOf(cashOutCount.getTotalShort()));

        addTable(document, table);
    }
}
