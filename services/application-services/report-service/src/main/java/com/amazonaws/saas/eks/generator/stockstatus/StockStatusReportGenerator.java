package com.amazonaws.saas.eks.generator.stockstatus;

import com.amazonaws.saas.eks.dto.requests.StockStatusReportRequest;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusItem;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.mapper.ReportMapper;
import com.amazonaws.saas.eks.model.StockLevelData;
import com.amazonaws.saas.eks.model.enums.StockLevel;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.saas.eks.repository.StockStatusRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockStatusReportGenerator implements Generator<StockStatusReportRequest, StockStatusReportResponse> {

    private static final String[] TABLE_HEADERS = {
            "Inv. Item #",
            "Description",
            "Status",
            "Vendor",
            "On Hand",
            "UOM",
            "Avg. Cost",
            "On Hand Value",
            "On Hand Retail"
    };

    @Autowired
    private StockStatusRepository repository;

    @Override
    public StockStatusReportResponse generate(String tenantId, StockStatusReportRequest request) {
        int from = request.getFrom() == null ? 0 : request.getFrom();
        int size = request.getSize() == null ? 0 : request.getSize();
        StockLevel stockLevel = StringUtils.hasLength(request.getStockLevel()) ? StockLevel.valueOfLabel(request.getStockLevel()) : null;
        StockLevelData data = repository.getStockStatusData(tenantId, from, size, request.getCategoryIds(),
                stockLevel, request.getItemStatus(), request.getVendor());

        List<String> productIds = data.getProducts().stream().map(Product::getId).collect(Collectors.toList());

        Map<String, UOM> uomMap = repository.getUOMs(tenantId, productIds);

        StockStatusReportResponse response = new StockStatusReportResponse();
        for (Product p : data.getProducts()) {
            StockStatusItem item = getStockStatusItem(p, uomMap);
            response.getItems().add(item);
        }
        response.setCount(data.getCount());
        return response;
    }

    @Override
    public byte[] generatePDF(String tenantId, StockStatusReportRequest request) throws DocumentException {
        // Setting Pagination values to get all items
        request.setFrom(0);
        request.setSize(0);
        StockStatusReportResponse data = generate(tenantId, request);

        // Setting up output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Building table
        PdfPTable table = new PdfPTable(9);
        addTableHeader(table);
        addRows(table, data);
        document.add(table);

        document.close();
        return outputStream.toByteArray();
    }

    private static StockStatusItem getStockStatusItem(Product p, Map<String, UOM> uomMap) {
        StockStatusItem item = ReportMapper.INSTANCE.productToStockStatusItem(p);
        if (item.getCost() != null) {
            if (item.getQuantityOnHand() != null) {
                item.setOnHandValue(item.getCost().multiply(BigDecimal.valueOf(item.getQuantityOnHand())));
            }
            if (p.getRetailPrice() != null) {
                item.setOnHandRetail(item.getCost().multiply(p.getRetailPrice()));
            }
        }
        if (uomMap.containsKey(p.getStockingUomId())) {
            item.setUom(uomMap.get(p.getStockingUomId()).getName());
        }
        return item;
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

    private void addRows(PdfPTable table, StockStatusReportResponse data) {
        for (StockStatusItem item : data.getItems()) {
            table.addCell(item.getSku());
            table.addCell(item.getName());
            table.addCell(item.getInventoryStatus());
            table.addCell(item.getVendorName());
            table.addCell(String.valueOf(item.getQuantityOnHand()));
            table.addCell(item.getUom());
            table.addCell(String.valueOf(item.getCost()));
            table.addCell(String.valueOf(item.getOnHandValue()));
            table.addCell(String.valueOf(item.getOnHandRetail()));
        }
    }
}
