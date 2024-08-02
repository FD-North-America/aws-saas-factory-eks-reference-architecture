package com.amazonaws.saas.eks.model.enums;

public enum ReportName {
    STOCK_STATUS("StockStatus"),
    CASH_DRAWER_CHECKOUT("CashDrawerCheckout"),
    SALES_TAX("SalesTax"),
    RETURNED_ITEMS("ReturnedItems"),
    PAID_OUT_CODES_AND_DISCOUNTS("PaidOutCodesAndDiscounts"),
    SALES_REGISTER("SalesRegister"),
    CATEGORY_SALES("CategorySales");

    private final String label;

    ReportName(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
