package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.*;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.CashDrawerCheckoutReportResponse;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.amazonaws.saas.eks.factory.ReportGeneratorFactory;
import com.amazonaws.saas.eks.service.ReportService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportGeneratorFactory reportGeneratorFactory;

    @Override
    public StockStatusReportResponse generateStockStatusReport(String tenantId, StockStatusReportRequest request) {
        var generator = reportGeneratorFactory.createStockStatusReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateStockStatusReportPDF(String tenantId, StockStatusReportRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createStockStatusReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public CashDrawerCheckoutReportResponse generateCashDrawerCheckoutReport(String tenantId,
                                                                             CashDrawerCheckoutReportRequest request) {
        var generator = reportGeneratorFactory.createCashDrawerCheckoutReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateCashDrawerCheckoutReportPDF(String tenantId, CashDrawerCheckoutReportRequest request)
            throws DocumentException {
        var generator = reportGeneratorFactory.createCashDrawerCheckoutReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public SalesTaxReportResponse generateSalesTaxReport(String tenantId, SalesTaxReportRequest request) {
        var generator = reportGeneratorFactory.createSalesTaxReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateSalesTaxReportPDF(String tenantId, SalesTaxReportRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createSalesTaxReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public ReturnedItemsReportResponse generateReturnedItemsReport(String tenantId, ReturnedItemsReportRequest request) {
        var generator = reportGeneratorFactory.createReturnedItemsReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateReturnedItemsReportPDF(String tenantId, ReturnedItemsReportRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createReturnedItemsReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public PaidOutCodesAndDiscountsReportResponse generatePaidOutCodesAndDiscountsReport(
            String tenantId, PaidOutCodesAndDiscountsReportRequest request) {
        var generator = reportGeneratorFactory.createPaidOutCodesAndDiscountsReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generatePaidOutCodesAndDiscountsReportPDF(String tenantId, PaidOutCodesAndDiscountsReportRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createPaidOutCodesAndDiscountsReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public SalesRegisterReportResponse generateSalesRegisterReport(String tenantId, SalesRegisterReportRequest request) {
        var generator = reportGeneratorFactory.createSalesRegisterReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateSalesRegisterReportPDF(String tenantId, SalesRegisterReportRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createSalesRegisterReportGenerator();
        return generator.generatePDF(tenantId, request);
    }

    @Override
    public CategorySalesReportResponse generateCategorySalesReport(String tenantId, CategorySaleRequest request) {
        var generator = reportGeneratorFactory.createCategorySalesReportGenerator();
        return generator.generate(tenantId, request);
    }

    @Override
    public byte[] generateCategorySalesReportPDF(String tenantId, CategorySaleRequest request) throws DocumentException {
        var generator = reportGeneratorFactory.createCategorySalesReportGenerator();
        return generator.generatePDF(tenantId, request);
    }
}
