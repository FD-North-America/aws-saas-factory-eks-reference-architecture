package com.amazonaws.saas.eks.factory.impl;

import com.amazonaws.saas.eks.dto.requests.*;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.CashDrawerCheckoutReportResponse;
import com.amazonaws.saas.eks.factory.ReportGeneratorFactory;
import com.amazonaws.saas.eks.generator.Generator;
import com.amazonaws.saas.eks.generator.cashdrawercheckout.CashDrawerCheckoutReportGenerator;
import com.amazonaws.saas.eks.generator.categorySales.CategorySalesReportGenerator;
import com.amazonaws.saas.eks.generator.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportGenerator;
import com.amazonaws.saas.eks.generator.returneditems.ReturnedItemsReportGenerator;
import com.amazonaws.saas.eks.generator.salesregister.SalesRegisterReportGenerator;
import com.amazonaws.saas.eks.generator.salestax.SalesTaxReportGenerator;
import com.amazonaws.saas.eks.generator.stockstatus.StockStatusReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportGeneratorFactoryImpl implements ReportGeneratorFactory {

    @Autowired
    private StockStatusReportGenerator stockStatusReportGenerator;

    @Autowired
    private CashDrawerCheckoutReportGenerator cashDrawerCheckoutReportGenerator;

    @Autowired
    private SalesTaxReportGenerator salesTaxReportGenerator;

    @Autowired
    private ReturnedItemsReportGenerator returnedItemsReportGenerator;

    @Autowired
    private PaidOutCodesAndDiscountsReportGenerator paidOutCodesReportGenerator;

    @Autowired
    private SalesRegisterReportGenerator salesRegisterReportGenerator;

    @Autowired
    private CategorySalesReportGenerator categorySalesReportGenerator;

    @Override
    public Generator<StockStatusReportRequest, StockStatusReportResponse> createStockStatusReportGenerator() {
        return stockStatusReportGenerator;
    }

    @Override
    public Generator<CashDrawerCheckoutReportRequest, CashDrawerCheckoutReportResponse> createCashDrawerCheckoutReportGenerator() {
        return cashDrawerCheckoutReportGenerator;
    }

    @Override
    public Generator<SalesTaxReportRequest, SalesTaxReportResponse> createSalesTaxReportGenerator() {
        return salesTaxReportGenerator;
    }

    @Override
    public Generator<ReturnedItemsReportRequest, ReturnedItemsReportResponse> createReturnedItemsReportGenerator() {
        return returnedItemsReportGenerator;
    }

    @Override
    public Generator<PaidOutCodesAndDiscountsReportRequest, PaidOutCodesAndDiscountsReportResponse> createPaidOutCodesAndDiscountsReportGenerator() {
        return paidOutCodesReportGenerator;
    }

    @Override
    public Generator<SalesRegisterReportRequest, SalesRegisterReportResponse> createSalesRegisterReportGenerator() {
        return salesRegisterReportGenerator;
    }

    @Override
    public Generator<CategorySaleRequest, CategorySalesReportResponse> createCategorySalesReportGenerator() {
        return categorySalesReportGenerator;
    }
}
