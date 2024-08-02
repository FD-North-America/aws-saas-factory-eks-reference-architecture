package com.amazonaws.saas.eks.factory;

import com.amazonaws.saas.eks.dto.requests.*;
import com.amazonaws.saas.eks.dto.responses.categorySales.CategorySalesReportResponse;
import com.amazonaws.saas.eks.dto.responses.paidoutcodesanddiscounts.PaidOutCodesAndDiscountsReportResponse;
import com.amazonaws.saas.eks.dto.responses.returneditems.ReturnedItemsReportResponse;
import com.amazonaws.saas.eks.dto.responses.salesregister.SalesRegisterReportResponse;
import com.amazonaws.saas.eks.dto.responses.salestax.SalesTaxReportResponse;
import com.amazonaws.saas.eks.dto.responses.stockstatus.StockStatusReportResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport.CashDrawerCheckoutReportResponse;
import com.amazonaws.saas.eks.generator.Generator;

public interface ReportGeneratorFactory {
    Generator<StockStatusReportRequest, StockStatusReportResponse> createStockStatusReportGenerator();

    Generator<CashDrawerCheckoutReportRequest, CashDrawerCheckoutReportResponse> createCashDrawerCheckoutReportGenerator();

    Generator<SalesTaxReportRequest, SalesTaxReportResponse> createSalesTaxReportGenerator();

    Generator<ReturnedItemsReportRequest, ReturnedItemsReportResponse> createReturnedItemsReportGenerator();

    Generator<PaidOutCodesAndDiscountsReportRequest, PaidOutCodesAndDiscountsReportResponse> createPaidOutCodesAndDiscountsReportGenerator();

    Generator<SalesRegisterReportRequest, SalesRegisterReportResponse> createSalesRegisterReportGenerator();

    Generator<CategorySaleRequest, CategorySalesReportResponse> createCategorySalesReportGenerator();
}
