package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.*;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.CashDrawerCheckoutReportResponse;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.itextpdf.text.DocumentException;

public interface ReportService {
    StockStatusReportResponse generateStockStatusReport(String tenantId, StockStatusReportRequest request);

    byte[] generateStockStatusReportPDF(String tenantId, StockStatusReportRequest request) throws DocumentException;

    CashDrawerCheckoutReportResponse generateCashDrawerCheckoutReport(String tenantId,
                                                                      CashDrawerCheckoutReportRequest request);

    byte[] generateCashDrawerCheckoutReportPDF(String tenantId,  CashDrawerCheckoutReportRequest request)
            throws DocumentException;

    SalesTaxReportResponse generateSalesTaxReport(String tenantId, SalesTaxReportRequest request);

    byte[] generateSalesTaxReportPDF(String tenantId, SalesTaxReportRequest request) throws DocumentException;

    ReturnedItemsReportResponse generateReturnedItemsReport(String tenantId, ReturnedItemsReportRequest request);

    byte[] generateReturnedItemsReportPDF(String tenantId, ReturnedItemsReportRequest request) throws DocumentException;

    PaidOutCodesAndDiscountsReportResponse generatePaidOutCodesAndDiscountsReport(
            String tenantId, PaidOutCodesAndDiscountsReportRequest request);

    byte[] generatePaidOutCodesAndDiscountsReportPDF(String tenantId, PaidOutCodesAndDiscountsReportRequest request) throws DocumentException;

    SalesRegisterReportResponse generateSalesRegisterReport(String tenantId, SalesRegisterReportRequest request);

    byte[] generateSalesRegisterReportPDF(String tenantId, SalesRegisterReportRequest request) throws DocumentException;

    CategorySalesReportResponse generateCategorySalesReport(String tenantId, CategorySaleRequest request);

    byte[] generateCategorySalesReportPDF(String tenantId, CategorySaleRequest request) throws DocumentException;
}
