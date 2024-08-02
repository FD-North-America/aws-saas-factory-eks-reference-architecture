package com.amazonaws.saas.eks.generator.salestax;

import com.amazonaws.saas.eks.dto.requests.SalesTaxReportRequest;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxItem;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxTotalItem;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.model.OrderData;
import com.amazonaws.saas.eks.model.opensearch.AggregationKeys;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.SalesTaxRepository;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.settings.model.Settings;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.TimeZone;

import static com.amazonaws.saas.eks.util.Utils.changeDateTimeZone;
import static com.amazonaws.saas.eks.util.Utils.createToDate;

@Service
public class SalesTaxReportGenerator implements Generator<SalesTaxReportRequest, SalesTaxReportResponse> {

    private static final String[] MAIN_TABLE_HEADERS = {
            "Invoice #",
            "Invoice Date",
            "Invoice Amt.",
            "Invoice Tax",
            "Invoice Tran. Amt.",
            "Non Taxable Sales",
            "Taxable Sales",
            "Tax Liability"
    };
    private static final String[] SUMMARY_TABLE_HEADERS = {
            "Tax Code",
            "Tax Code Desc",
            "Tax Rate",
            "Gross",
            "Non Taxable",
            "Taxable",
            "Tax Due"
    };

    // TODO: HANDLE MULTIPLE TAX CODES
    private static final String TAX_CODE = "Ohio";

    @Autowired
    private SalesTaxRepository repository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public SalesTaxReportResponse generate(String tenantId, SalesTaxReportRequest request) {
        int from = request.getFrom() == null ? 0 : request.getFrom();
        int size = request.getSize() == null ? 0 : request.getSize();
        Settings settings = settingsRepository.get(tenantId);

        if (request.getToDate() == null) {
            request.setToDate(createToDate(settings.getTimeZone()));
        }
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(settings.getTimeZone()));

        OrderData data = repository.getSalesTaxData(tenantId, request.getFromDate(), request.getToDate(), from, size);

        SalesTaxReportResponse response = new SalesTaxReportResponse();
        response.setCount(data.getCount());
        for (Order o : data.getOrders()) {
            SalesTaxItem item = new SalesTaxItem();
            item.setInvoiceAmount(o.getSubTotal());
            item.setInvoiceTax(o.getTaxTotal());
            item.setTaxLiability(o.getTaxTotal());
            item.setTaxableSales(o.getTaxableSubTotal());
            try {
                item.setInvoiceDate(changeDateTimeZone(o.getPaidDate(), timeZone));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            item.setInvoiceNumber(o.getNumber());
            item.setNonTaxableSales(o.getNonTaxableSubTotal());
            item.setInvoiceTransactionAmount(o.getTotal());
            response.getItems().add(item);
        }

        SalesTaxTotalItem totalItem = new SalesTaxTotalItem();
        totalItem.setTaxCode(TAX_CODE);
        totalItem.setTaxCodeDescription(TAX_CODE);
        totalItem.setTaxRate(settings.getTaxRate());
        totalItem.setGross(data.getAggregationMap().get(AggregationKeys.SUM_SUB_TOTAL));
        totalItem.setTaxable(data.getAggregationMap().get(AggregationKeys.SUM_TAXABLE_SUBTOTAL));
        totalItem.setNonTaxable(data.getAggregationMap().get(AggregationKeys.SUM_NON_TAXABLE_TOTAL));
        totalItem.setTaxDue(data.getAggregationMap().get(AggregationKeys.SUM_TAX_DUE));
        response.getTotals().add(totalItem);
        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, SalesTaxReportRequest request) throws DocumentException {
        request.setFrom(0);
        request.setSize(0);
        SalesTaxReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Building main table
        PdfPTable mainTable = new PdfPTable(8);
        addTableHeader(mainTable, MAIN_TABLE_HEADERS);
        addMainRows(mainTable, data);
        document.add(mainTable);

        // Building summary table
        PdfPTable summaryTable = new PdfPTable(7);
        addTableHeader(summaryTable, SUMMARY_TABLE_HEADERS);
        addSummaryRows(summaryTable, data);
        document.add(summaryTable);

        document.close();
        return outputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String columnTitle : headers) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        }
    }

    private void addMainRows(PdfPTable table, SalesTaxReportResponse data) {
        for (SalesTaxItem item : data.getItems()) {
            table.addCell(item.getInvoiceNumber());
            table.addCell(String.valueOf(item.getInvoiceDate()));
            table.addCell(String.valueOf(item.getInvoiceAmount()));
            table.addCell(String.valueOf(item.getInvoiceTax()));
            table.addCell(String.valueOf(item.getInvoiceTransactionAmount()));
            table.addCell(String.valueOf(item.getNonTaxableSales()));
            table.addCell(String.valueOf(item.getTaxableSales()));
            table.addCell(String.valueOf(item.getTaxLiability()));
        }
    }

    private void addSummaryRows(PdfPTable table, SalesTaxReportResponse data) {
        for (SalesTaxTotalItem item : data.getTotals()) {
            table.addCell(item.getTaxCode());
            table.addCell(item.getTaxCodeDescription());
            table.addCell(String.valueOf(item.getTaxRate()));
            table.addCell(String.valueOf(item.getGross()));
            table.addCell(String.valueOf(item.getNonTaxable()));
            table.addCell(String.valueOf(item.getTaxable()));
            table.addCell(String.valueOf(item.getTaxDue()));
        }
    }
}
