package com.amazonaws.saas.eks.generator.salesregister;

import com.amazonaws.saas.eks.dto.requests.SalesRegisterReportRequest;
import com.amazonaws.saas.eks.dto.responses.salesregister.CashItem;
import com.amazonaws.saas.eks.dto.responses.salesregister.ReportDetailTotal;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterItem;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.model.OrderData;
import com.amazonaws.saas.eks.model.opensearch.AggregationKeys;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.SalesRegisterRepository;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.settings.model.Settings;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.amazonaws.saas.eks.util.PDFUtils.*;
import static com.amazonaws.saas.eks.util.Utils.*;

@Service
public class SalesRegisterReportGenerator
        implements Generator<SalesRegisterReportRequest, SalesRegisterReportResponse> {

    private static final String POS_INVOICE_TOTAL_NAME = "posInvoices";
    private static final String LESS_PAID_OUT_NAME = "lessPaidOut";
    private static final String GRAND_TOTALS = "grandTotals";
    private static final String NON_TAX_SALES = "nonTaxSales";
    private static final String TAXABLE_SALES = "taxableSales";
    private static final String SALES = "sales";
    private static final String PAID_OUTS = "paidOuts";
    private static final String TAX_TOTAL = "taxTotal";
    private static final String TOTAL = "total";

    @Autowired
    private SalesRegisterRepository repository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public SalesRegisterReportResponse generate(String tenantId, SalesRegisterReportRequest request) {
        int from = request.getFrom() == null ? 0 : request.getFrom();
        int size = request.getSize() == null ? 0 : request.getSize();

        Settings settings = settingsRepository.get(tenantId);
        if (request.getInvoiceToDate() == null) {
            request.setInvoiceToDate(createToDate(settings.getTimeZone()));
        }
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(settings.getTimeZone()));

        OrderData data = repository.getSalesRegisterData(tenantId, request.getInvoiceFromDate(), request.getInvoiceToDate(),
                request.getInvoiceNumberFrom(), request.getInvoiceNumberTo(), request.getSalesRep(),
                from, size);

        // Build Main Table
        SalesRegisterReportResponse response = new SalesRegisterReportResponse();
        response.setCount(data.getCount());
        for (Order o : data.getOrders()) {
            SalesRegisterItem item = getSalesRegisterItem(tenantId, o, timeZone);
            response.getItems().add(item);
        }

        // Build Cash Sales and Discount values
        BigDecimal cashTransactionCount = data.getAggregationMap().get(AggregationKeys.SUM_CASH_TRANSACTION_COUNT);
        response.setCashSales(cashTransactionCount);
        response.setImmediateDiscounts(data.getAggregationMap().get(AggregationKeys.SUM_DISCOUNT_TOTAL));

        // Build Totals Table
        ReportDetailTotal posInvoiceTotal = ReportDetailTotal.builder()
                .type(POS_INVOICE_TOTAL_NAME)
                .sales(data.getAggregationMap().get(AggregationKeys.SUM_SALES_TOTAL))
                .taxTotals(data.getAggregationMap().get(AggregationKeys.SUM_TAX_TOTAL))
                .invAmount(data.getAggregationMap().get(AggregationKeys.SUM_INVOICE_TOTAL))
                .profit(data.getAggregationMap().get(AggregationKeys.SUM_PROFIT_TOTAL))
                .margin(data.getAggregationMap().get(AggregationKeys.SUM_MARGIN_TOTAL))
                .build();
        ReportDetailTotal lessPaidOut = ReportDetailTotal.builder()
                .type(LESS_PAID_OUT_NAME)
                .sales(data.getAggregationMap().get(AggregationKeys.SUM_PAID_OUT_TOTAL))
                .invAmount(data.getAggregationMap().get(AggregationKeys.SUM_PAID_OUT_TOTAL))
                .build();
        ReportDetailTotal grandTotals = ReportDetailTotal.builder()
                .type(GRAND_TOTALS)
                .sales(posInvoiceTotal.getSales().subtract(lessPaidOut.getSales()))
                .invAmount(posInvoiceTotal.getInvAmount().subtract(lessPaidOut.getInvAmount()))
                .build();

        response.setTotalItems(Arrays.asList(posInvoiceTotal, lessPaidOut, grandTotals));

        // Build Cash Table
        BigDecimal nonTaxable = data.getAggregationMap().get(AggregationKeys.SUM_NON_TAXABLE_TOTAL);
        BigDecimal returnNonTaxable = data.getAggregationMap().get(AggregationKeys.SUM_RETURN_NON_TAXABLE_SUB_TOTAL);
        CashItem nonTaxableItem = CashItem.builder()
                .type(NON_TAX_SALES)
                .receipts(nonTaxable)
                .returns(returnNonTaxable)
                .cashTotals(nonTaxable.add(returnNonTaxable))
                .build();
        BigDecimal taxable = data.getAggregationMap().get(AggregationKeys.SUM_TAXABLE_SUBTOTAL);
        BigDecimal returnTaxable = data.getAggregationMap().get(AggregationKeys.SUM_RETURN_TAXABLE_SUB_TOTAL);
        CashItem taxableItem = CashItem.builder()
                .type(TAXABLE_SALES)
                .receipts(taxable)
                .returns(returnTaxable)
                .cashTotals(taxable.add(returnTaxable))
                .build();
        BigDecimal salesTotal = data.getAggregationMap().get(AggregationKeys.SUM_SUB_TOTAL);
        BigDecimal returnSales = data.getAggregationMap().get(AggregationKeys.SUM_RETURN_TOTAL);
        CashItem salesItem = CashItem.builder()
                .type(SALES)
                .receipts(salesTotal)
                .returns(returnSales)
                .cashTotals(salesTotal.add(returnSales))
                .build();
        BigDecimal paidOutTotal = data.getAggregationMap().get(AggregationKeys.SUM_PAID_OUT_TOTAL);
        CashItem paidOutsItem = CashItem.builder()
                .type(PAID_OUTS)
                .receipts(paidOutTotal)
                .cashTotals(paidOutTotal)
                .build();
        BigDecimal taxTotal = data.getAggregationMap().get(AggregationKeys.SUM_TAX_TOTAL);
        BigDecimal returnTaxTotal = data.getAggregationMap().get(AggregationKeys.SUM_RETURN_TAX_TOTAL);
        CashItem taxTotalItem = CashItem.builder()
                .type(TAX_TOTAL)
                .receipts(taxTotal)
                .returns(returnTaxTotal)
                .cashTotals(taxTotal.add(returnTaxTotal))
                .build();
        BigDecimal receiptsTotal = salesTotal.subtract(paidOutTotal).add(taxTotal);
        BigDecimal returnsTotal = returnSales.add(returnTaxTotal);
        CashItem totalItem = CashItem.builder()
                .type(TOTAL)
                .receipts(receiptsTotal)
                .returns(returnsTotal)
                .cashTotals(receiptsTotal.add(returnsTotal))
                .build();
        response.setCashItems(Arrays.asList(nonTaxableItem, taxableItem, salesItem, paidOutsItem, taxTotalItem, totalItem));

        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, SalesRegisterReportRequest request) throws DocumentException {
        SalesRegisterReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        addHeaderText(document, "Total: " + data.getItems().size());

        addMainTable(document, data);

        addHeaderText(document, "Report Detail Totals");

        addDetailTotalsTable(document, data);

        addHeaderTextMedium(document, "Cash Sales: " + data.getCashSales().setScale(0, RoundingMode.CEILING));
        addHeaderTextMedium(document, "Immediate Discounts: " + data.getImmediateDiscounts().setScale(0, RoundingMode.CEILING));

        addHeaderText(document, "Cash");

        addCashTable(document, data);

        document.close();
        return outputStream.toByteArray();
    }

    private SalesRegisterItem getSalesRegisterItem(String tenantId, Order o, TimeZone timeZone) {
        SalesRegisterItem item = new SalesRegisterItem();
        // TODO: HANDLE MULTIPLE BRANCHES
        item.setBranch(tenantId);
        item.setInvoiceNumber(o.getNumber());
        try {
            item.setInvoiceDate(changeDateTimeZone(o.getPaidDate(), timeZone));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        item.setSales(o.getSubTotal());
        item.setCost(o.getTotalCost());
        item.setTaxTotals(o.getTaxTotal());
        item.setInvoiceAmount(o.getTotal());
        item.setProfit(o.getProfit());
        item.setMargin(o.getMargin());
        item.setSalesRep(o.getSalesRep());
        return item;
    }

    private void addMainTable(Document document, SalesRegisterReportResponse data) throws DocumentException {
        PdfPTable table = new PdfPTable(10);
        addMainTableHeader(table);
        addMainTableRows(table, data);
        addTable(document, table);
    }

    private void addMainTableHeader(PdfPTable table) {
        final String[] tableHeader = { "Branch", "Invoice #", "Inv. Date", "Sales", "Tax Totals",
                "Inv. Amt.", "Cost", "Profit", "Margin", "Sales Rep."
        };
        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }
    }

    private void addMainTableRows(PdfPTable table, SalesRegisterReportResponse data) {
        for (SalesRegisterItem item: data.getItems()) {
            addTableCellACenter(table, item.getBranch());
            addTableCellACenter(table, item.getInvoiceNumber());
            addTableCellACenter(table, String.valueOf(item.getInvoiceDate()));
            addTableCellARight(table, String.valueOf(roundValue(item.getSales())));
            addTableCellARight(table, String.valueOf(roundValue(item.getTaxTotals())));
            addTableCellARight(table, String.valueOf(roundValue(item.getInvoiceAmount())));
            addTableCellARight(table, String.valueOf(roundValue(item.getCost())));
            addTableCellARight(table, String.valueOf(roundValue(item.getProfit())));
            addTableCellARight(table, roundValue(item.getMargin()) + " %");
            addTableCellACenter(table, item.getSalesRep());
        }
    }

    private void addDetailTotalsTable(Document document, SalesRegisterReportResponse data) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        addDetailTotalsTableHeader(table);
        addDetailTotalsTableRows(table, data);
        addTable(document, table);
    }

    private void addDetailTotalsTableHeader(PdfPTable table) {
        final String[] tableHeader = { "", "Sales", "Tax Totals", "Inv. Amt.", "Profit", "Margin" };
        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }
    }

    private void addDetailTotalsTableRows(PdfPTable table, SalesRegisterReportResponse data) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(POS_INVOICE_TOTAL_NAME, "POS Invoices");
        typeMap.put(LESS_PAID_OUT_NAME, "Less Paid Out");
        typeMap.put(GRAND_TOTALS, "Grand Totals");

        for (ReportDetailTotal item : data.getTotalItems()) {
            addTableHeaderCell(table, typeMap.containsKey(item.getType()) ? typeMap.get(item.getType()) : item.getType());
            addTableCellARight(table, String.valueOf(roundValue(item.getSales())));
            addTableCellARight(table, String.valueOf(roundValue(item.getTaxTotals())));
            addTableCellARight(table, String.valueOf(roundValue(item.getInvAmount())));
            addTableCellARight(table, String.valueOf(roundValue(item.getProfit())));
            addTableCellARight(table, roundValue(item.getMargin()) + " %");
        }
    }

    private void addCashTable(Document document, SalesRegisterReportResponse data) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        addCashTableHeader(table);
        addCashTableRows(table, data);
        addTable(document, table);
    }

    private void addCashTableHeader(PdfPTable table) {
        final String[] tableHeader = { "", "Receipts", "Returns", "Cash Totals" };
        for (String columnTitle : tableHeader) {
            addTableHeaderCellACenter(table, columnTitle);
        }
    }

    private void addCashTableRows(PdfPTable table, SalesRegisterReportResponse data) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(NON_TAX_SALES, "NonTax Sales");
        typeMap.put(TAXABLE_SALES, "Taxable Sales");
        typeMap.put(SALES, "Sales (Item Total)");
        typeMap.put(PAID_OUTS, "Less Discounts");
        typeMap.put(TAX_TOTAL, "Total Tax");
        typeMap.put(TOTAL, "Total");

        for (CashItem item : data.getCashItems()) {
            addTableHeaderCell(table, typeMap.containsKey(item.getType()) ? typeMap.get(item.getType()) : item.getType());
            addTableCellARight(table, String.valueOf(roundValue(item.getReceipts())));
            addTableCellARight(table, String.valueOf(roundValue(item.getReturns())));
            addTableCellARight(table, String.valueOf(roundValue(item.getCashTotals())));
        }
    }
}
