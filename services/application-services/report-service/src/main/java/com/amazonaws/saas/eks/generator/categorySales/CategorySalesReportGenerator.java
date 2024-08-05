package com.amazonaws.saas.eks.generator.categorySales;

import com.amazonaws.saas.eks.dto.requests.CategorySaleRequest;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySaleResponse;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.categorySales.ItemRow;
import com.amazonaws.saas.eks.dto.responses.categorySales.TotalRow;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.order.model.CategoryOrder;
import com.amazonaws.saas.eks.order.model.CategorySale;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.product.model.Category;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.saas.eks.repository.CategorySalesRepository;
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
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.amazonaws.saas.eks.util.PDFUtils.*;
import static com.amazonaws.saas.eks.util.Utils.createToDate;

@Service
public class CategorySalesReportGenerator implements Generator<CategorySaleRequest, CategorySalesReportResponse> {

    private static final String[] TABLE_HEADERS = {
            "ITEM",
            "DESCRIPTION",
            "QTY SOLD",
            "PRICING UOM",
            "QTY ON HAND",
            "STOCKING UOM",
            "MIN ON HAND QTY",
            "TOTAL $$ SALES"
    };
    private static final String[] CUSTOM_HEADERS = {
      "COST",
      "PROFIT",
      "MARGIN"
    };
    private static final int COLUMN_COUNT = 8;
    private static final String TOTALS_NAME = "TOTALS ALL CATEGORIES";
    private static final String REPORT_NAME = "CATEGORY SALES REPORT";
    private static final int MONTH_OFFSET = 1;
    private static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Autowired
    private CategorySalesRepository repository;
    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public CategorySalesReportResponse generate(String tenantId, CategorySaleRequest request) {
        boolean displayCost = request.getDisplayCost() != null ? request.getDisplayCost() : false;
        boolean displayProfitMargin = request.getDisplayProfitMargin() != null ? request.getDisplayProfitMargin() : false;
        Settings settings = settingsRepository.get(tenantId);
        if (request.getToDate() == null) {
            request.setToDate(createToDate(settings.getTimeZone()));
        }

        // Category sales are stored by month so adding a month to get all orders and filtering later by exact dates
        ZonedDateTime toDateMonth = request.getToDate().plusMonths(MONTH_OFFSET);

        // Fetch all the sales for all categories within the given time frame
        List<CategorySale> categorySales = repository.getCategorySales(tenantId, request.getFromDate(), toDateMonth);
        Set<String> categorySaleIds = categorySales.stream().map(CategorySale::getCategoryId).collect(Collectors.toSet());

        // Parse out the order IDs within the given time frame
        Date fromDate = Date.from(request.getFromDate().toInstant());
        Date toDate = Date.from(request.getToDate().toInstant());
        Set<String> orderIds = new HashSet<>();
        for (CategorySale sale : categorySales) {
            for (CategoryOrder order : sale.getOrders()) {
                if (order.getDate().after(fromDate) && order.getDate().before(toDate)) {
                    orderIds.add(order.getOrderId());
                }
            }
        }

        // Fetch details
        List<Category> allCategories = repository.getAllCategories(tenantId);
        List<Order> orders = repository.getOrders(tenantId, orderIds);

        // Initializing maps for faster retrieval
        Map<String, Category> categoryMap = allCategories.stream().collect(Collectors.toMap(Category::getId, c->c));
        Map<String, List<Product>> categoryProductsMap = new HashMap<>();

        // Filter out any categories that don't fit in the range
        Set<String> matchingCategoryIds = new HashSet<>();
        for (Category c : allCategories) {
            if (categorySaleIds.contains(c.getId()) && isValidCategory(request, c)) {
                matchingCategoryIds.add(c.getId());
            }
        }

        // Find all the products that fit the filtered categories and calculate totals
        Set<String> productIds = new HashSet<>();
        Map<String, ProductOrderTotals> productTotalsMap = new HashMap<>();
        for (Order o : orders) {
            for (LineItem l : o.getLineItems()) {
                if (isValidProductType(l) && matchingCategoryIds.contains(l.getCategoryId())) {
                    if (isValidProduct(request, l)) {
                        productIds.add(l.getId());

                        // calculate totals while we're looping through the orders
                        BigDecimal totalSales = l.getExtendedPrice();
                        BigDecimal totalCost = l.getCost().multiply(BigDecimal.valueOf(l.getQuantity()));
                        BigDecimal profit = totalSales.subtract(totalCost);
                        if (productTotalsMap.containsKey(l.getId())) {
                            ProductOrderTotals totals = productTotalsMap.get(l.getId());
                            totals.setQuantitySold(totals.getQuantitySold() + l.getQuantity());
                            totals.setTotalSales(totals.getTotalSales().add(totalSales));
                            totals.setCost(totals.getCost().add(totalCost));
                            totals.setProfit(totals.getProfit().add(profit));
                            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                                totals.setGrossMargin(totals.getProfit().divide(totalSales, ROUNDING_MODE));
                            }
                        } else {
                            ProductOrderTotals totals = new ProductOrderTotals();
                            totals.setQuantitySold(l.getQuantity());
                            totals.setTotalSales(totalSales);
                            totals.setCost(totalCost);
                            totals.setProfit(profit);
                            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                                totals.setGrossMargin(totals.getProfit().divide(totalSales, ROUNDING_MODE));
                            }
                            productTotalsMap.put(l.getId(), totals);
                        }
                    }
                }
            }
        }

        // Fetch Product Details
        List<Product> products = repository.getProducts(tenantId, productIds);
        Set<String> uomIds = new HashSet<>();
        for (Product p : products) {
            categoryProductsMap.computeIfAbsent(p.getCategoryId(), k -> new ArrayList<>()).add(p);
            uomIds.add(p.getPricingUomId());
            uomIds.add(p.getStockingUomId());
        }

        // Fetch UOM Details
        Map<String, UOM> uomMap = repository.getUOM(tenantId, uomIds);

        // Loop through and build out the response
        CategorySalesReportResponse response = new CategorySalesReportResponse();
        TotalRow allCategoryTotals = new TotalRow();
        allCategoryTotals.setName(TOTALS_NAME);
        for (String categoryId : categoryProductsMap.keySet()) {
            Category c = categoryMap.get(categoryId);
            CategorySaleResponse categorySaleResponse = new CategorySaleResponse();
            categorySaleResponse.setPath(buildPath(categoryMap, c));
            categorySaleResponse.setCategoryName(String.format("%s %s", c.getCode(), categorySaleResponse.getPath()));
            TotalRow totalRow = new TotalRow();
            for (Product p : categoryProductsMap.get(categoryId)) {
                float quantityOnHand = p.getQuantityOnHand() == null ? 0 : p.getQuantityOnHand();
                float minOnHandQuantity = p.getMinQtyOnHand() == null ? 0 : p.getMinQtyOnHand();
                ProductOrderTotals totals = productTotalsMap.get(p.getId());
                ItemRow row = new ItemRow();
                row.setId(p.getSku());
                row.setDescription(p.getName());
                row.setQuantitySold(totals.getQuantitySold());
                if (uomMap.containsKey(p.getPricingUomId())) {
                    row.setPricingUom(uomMap.get(p.getPricingUomId()).getName());
                }
                if (uomMap.containsKey(p.getStockingUomId())) {
                    row.setStockingUom(uomMap.get(p.getStockingUomId()).getName());
                }
                row.setQuantityOnHand(quantityOnHand);
                row.setMinOnHandQuantity(minOnHandQuantity);
                row.setTotalSales(totals.getTotalSales());

                // Totals for the whole Category
                totalRow.setName(String.format("%s %s", c.getName(), c.getCode()));
                totalRow.setQuantitySold(totalRow.getQuantitySold() + totals.getQuantitySold());
                totalRow.setQuantityOnHand(totalRow.getQuantityOnHand() + quantityOnHand);
                totalRow.setTotalSales(totalRow.getTotalSales().add(totals.getTotalSales()));

                allCategoryTotals.setQuantitySold(allCategoryTotals.getQuantitySold() + totals.getQuantitySold());
                allCategoryTotals.setQuantityOnHand(allCategoryTotals.getQuantityOnHand() + quantityOnHand);
                allCategoryTotals.setTotalSales(allCategoryTotals.getTotalSales().add(totals.getTotalSales()));

                if (displayCost) {
                    row.setCost(totals.getCost());
                    totalRow.setCost(totalRow.getCost().add(totals.getCost()));
                    allCategoryTotals.setCost(allCategoryTotals.getCost().add(totals.getCost()));
                }
                if (displayProfitMargin) {
                    row.setProfit(totals.getProfit());
                    row.setGrossMargin(totals.getGrossMargin());
                    totalRow.setProfit(totalRow.getProfit().add(totals.getProfit()));
                    if (totalRow.getTotalSales().compareTo(BigDecimal.ZERO) > 0) {
                        totalRow.setGrossMargin(totalRow.getProfit().divide(totalRow.getTotalSales(), ROUNDING_MODE));
                    }
                    allCategoryTotals.setProfit(allCategoryTotals.getProfit().add(totals.getProfit()));
                    if (allCategoryTotals.getTotalSales().compareTo(BigDecimal.ZERO) > 0) {
                        allCategoryTotals.setGrossMargin(allCategoryTotals.getProfit().divide(
                                allCategoryTotals.getTotalSales(),
                                ROUNDING_MODE));
                    }
                }
                categorySaleResponse.getItems().add(row);
            }

            categorySaleResponse.setTotals(totalRow);
            response.getCategories().add(categorySaleResponse);
        }
        response.setAllCategoryTotals(allCategoryTotals);

        return response;
    }

    private static boolean isValidCategory(CategorySaleRequest request, Category c) {
        return (request.getFromCategoryNumber() == null || c.getCode().compareTo(request.getFromCategoryNumber()) >= 0) &&
                (request.getToCategoryNumber() == null || c.getCode().compareTo(request.getToCategoryNumber()) <= 0);
    }

    private static boolean isValidProduct(CategorySaleRequest request, LineItem l) {
        return (request.getFromProductNumber() == null || l.getSku().compareTo(request.getFromProductNumber()) >= 0) &&
                (request.getToProductNumber() == null || l.getSku().compareTo(request.getToProductNumber()) <= 0);
    }

    private static boolean isValidProductType(LineItem l) {
        return l.getType().equals(LineItemType.PRODUCT.toString()) ||
                l.getType().equals(LineItemType.GENERIC.toString());
    }

    private static String buildPath(Map<String, Category> categoryMap, Category c) {
        List<String> pathIds = List.of(c.getCategoryPath().split(Product.KEY_DELIMITER));
        List<String> pathNames = new ArrayList<>();
        for (String pathId : pathIds) {
            Category pathCategory = categoryMap.get(pathId);
            pathNames.add(pathCategory.getName());
        }
        return String.join("/", pathNames);
    }

    @Override
    public byte[] generatePDF(String tenantId, CategorySaleRequest request) throws DocumentException {
        boolean displayCost = request.getDisplayCost() != null ? request.getDisplayCost() : false;
        boolean displayProfitMargin = request.getDisplayProfitMargin() != null ? request.getDisplayProfitMargin() : false;
        CategorySalesReportResponse data = generate(tenantId, request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        int columnCount = COLUMN_COUNT;
        if (displayCost) {
            columnCount++;
        }
        if (displayProfitMargin) {
            columnCount += 2;
        }

        addHeaderText(document, REPORT_NAME);

        PdfPTable table = new PdfPTable(columnCount);

        for (String title : TABLE_HEADERS) {
            addTableHeaderCellACenter(table, title);
        }
        if (displayCost) {
            addTableHeaderCellACenter(table, CUSTOM_HEADERS[0]);

        }
        if (displayProfitMargin) {
            addTableHeaderCellACenter(table, CUSTOM_HEADERS[1]);
            addTableHeaderCellACenter(table, CUSTOM_HEADERS[2]);
        }
        for (CategorySaleResponse c : data.getCategories()) {
            addTableHeaderRow(table, c.getCategoryName(), columnCount);
            for (ItemRow i : c.getItems()) {
                addTableCellACenter(table, i.getId());
                addTableCellACenter(table, i.getDescription());
                addTableCellACenter(table, String.valueOf(i.getQuantitySold()));
                addTableCellACenter(table, i.getPricingUom());
                addTableCellACenter(table, String.valueOf(i.getQuantityOnHand()));
                addTableCellACenter(table, i.getStockingUom());
                addTableCellACenter(table, String.valueOf(i.getMinOnHandQuantity()));
                addTableCellACenter(table, String.valueOf(i.getTotalSales()));
                if (displayCost) {
                    addTableCellACenter(table, String.valueOf(i.getCost()));
                }
                if (displayProfitMargin) {
                    addTableCellACenter(table, String.valueOf(i.getProfit()));
                    addTableCellACenter(table, formatPercentage(i.getGrossMargin()));
                }
            }
            TotalRow row = c.getTotals();
            addTableHeaderRow(table, String.format("%s - %s", "TOTALS", c.getCategoryName()), columnCount);
            addTableCellACenter(table, "");
            addTableCellACenter(table, "");
            addTableCellACenter(table, String.valueOf(row.getQuantitySold()));
            addTableCellACenter(table, "");
            addTableCellACenter(table, String.valueOf(row.getQuantityOnHand()));
            addTableCellACenter(table, "");
            addTableCellACenter(table, "");
            addTableCellACenter(table, String.valueOf(row.getTotalSales()));

            if (displayCost) {
                addTableCellACenter(table, String.valueOf(row.getCost()));
            }
            if (displayProfitMargin) {
                addTableCellACenter(table, String.valueOf(row.getProfit()));
                addTableCellACenter(table, formatPercentage(row.getGrossMargin()));
            }
        }

        addTableHeaderRow(table, TOTALS_NAME, columnCount);
        TotalRow allTotals = data.getAllCategoryTotals();
        addTableCellACenter(table, "");
        addTableCellACenter(table, "");
        addTableCellACenter(table, String.valueOf(allTotals.getQuantitySold()));
        addTableCellACenter(table, "");
        addTableCellACenter(table, String.valueOf(allTotals.getQuantityOnHand()));
        addTableCellACenter(table, "");
        addTableCellACenter(table, "");
        addTableCellACenter(table, String.valueOf(allTotals.getTotalSales()));

        if (displayCost) {
            addTableCellACenter(table, String.valueOf(allTotals.getCost()));
        }
        if (displayProfitMargin) {
            addTableCellACenter(table, String.valueOf(allTotals.getProfit()));
            addTableCellACenter(table, formatPercentage(allTotals.getGrossMargin()));
        }

        addTable(document, table);
        document.close();
        return outputStream.toByteArray();
    }

    private static String formatPercentage(BigDecimal value) {
        return new DecimalFormat("#.##%").format(value);
    }
}
