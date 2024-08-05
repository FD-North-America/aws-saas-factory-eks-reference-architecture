package com.amazonaws.saas.eks.generator.returneditems;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.dto.requests.ReturnedItemsReportRequest;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItem;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.model.enums.ReportName;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.repository.ReturnedItemsRepository;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.settings.model.Settings;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.util.Utils.changeDateTimeZone;
import static com.amazonaws.saas.eks.util.Utils.createToDate;

@Service
public class ReturnedItemsReportGenerator implements Generator<ReturnedItemsReportRequest, ReturnedItemsReportResponse> {
    private static final Logger logger = LogManager.getLogger(ReturnedItemsReportGenerator.class);

    private static final String[] TABLE_HEADERS = {
            "Item Number",
            "Description",
            "Invoice",
            "Date",
            "Qty",
            "Price UOM",
            "Extension"
    };

    @Autowired
    private ReturnedItemsRepository reportRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public ReturnedItemsReportResponse generate(String tenantId, ReturnedItemsReportRequest request) {
        logger.info("Generating report {}", ReportName.RETURNED_ITEMS);

        ReturnedItemsReportResponse response = new ReturnedItemsReportResponse();

        Settings settings = settingsRepository.get(tenantId);
        if (request.getToDate() == null) {
            request.setToDate(createToDate(settings.getTimeZone()));
        }
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(settings.getTimeZone()));

        boolean filterBySalesRep = StringUtils.hasLength(request.getSalesRep());
        Set<String> selectedCashDrawers = new HashSet<>();
        if (filterBySalesRep) {
            response.setSalesRep(request.getSalesRep());

            List<CashDrawer> cashDrawers = reportRepository.getCashDrawersByAssignedUser(tenantId, request.getSalesRep());
            if (cashDrawers.isEmpty()) {
                return response;
            }

            selectedCashDrawers = cashDrawers.stream().map(CashDrawer::getId).collect(Collectors.toSet());
        }

        List<Order> orders = reportRepository.getOrdersWithReturns(tenantId, request.getFromDate(), request.getToDate());

        BigDecimal total = BigDecimal.ZERO;
        for (Order o : orders) {
            if (!filterBySalesRep || selectedCashDrawers.contains(o.getCashDrawerId())) {
                for (LineItem l : o.getLineItems()) {
                    ReturnedItem item = buildReturnedItem(o, l, timeZone);
                    response.getReturnedItems().add(item);
                    total = total.add(item.getExtension());
                }
            }
        }
        response.setTotal(total);

        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, ReturnedItemsReportRequest request) throws DocumentException {
        ReturnedItemsReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(7);
        addTableHeader(table);
        addRows(table, data);
        addTotalRow(table, data);
        document.add(table);

        document.close();
        return outputStream.toByteArray();
    }

    private ReturnedItem buildReturnedItem(Order order, LineItem lineItem, TimeZone timeZone) {
        ReturnedItem returnedItem = new ReturnedItem();
        returnedItem.setNumber(lineItem.getSku());
        returnedItem.setDescription(lineItem.getName());
        returnedItem.setInvoice(order.getNumber());
        try {
            returnedItem.setDate(changeDateTimeZone(order.getCreated(), timeZone));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        returnedItem.setQuantity(lineItem.getQuantity());
        returnedItem.setPriceUOM(lineItem.getPrice());
        returnedItem.setExtension(lineItem.getExtendedPrice());
        if (lineItem.getTaxAmount() != null) {
            returnedItem.setExtension(returnedItem.getExtension().add(lineItem.getTaxAmount()));
        }
        return returnedItem;
    }

    private void addTableHeader(PdfPTable table) {
        for (String columnTitle : TABLE_HEADERS) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        }
    }

    private void addRows(PdfPTable table, ReturnedItemsReportResponse data) {
        for (ReturnedItem item : data.getReturnedItems()) {
            table.addCell(item.getNumber());
            table.addCell(item.getDescription());
            table.addCell(item.getInvoice());
            table.addCell(String.valueOf(item.getDate()));
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getPriceUOM()));
            table.addCell(String.valueOf(item.getExtension()));
        }
    }

    private void addTotalRow(PdfPTable table, ReturnedItemsReportResponse data) {
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("Total");
        table.addCell(String.valueOf(data.getTotal()));
    }
}
