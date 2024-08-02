package com.amazonaws.saas.eks.cashdrawer.model;

public class Permission {
    public static final String CASH_DRAWER_READ = "cash-drawer-checkout-management-read";
    public static final String CASH_DRAWER_CREATE = "cash-drawer-checkout-management-create";
    public static final String CASH_DRAWER_UPDATE = "cash-drawer-checkout-management-update";
    public static final String CASH_DRAWER_DELETE = "cash-drawer-checkout-management-delete";

    public static final String CASH_DRAWER_CHECKOUT_READ = "pos-daily-management-recap-read";
    public static final String CASH_DRAWER_CHECKOUT_UPDATE = "pos-daily-management-recap-update";
    public static final String CASH_DRAWER_CHECKOUT_CREATE = "pos-daily-management-recap-create";
    public static final String CASH_DRAWER_CHECKOUT_DELETE = "pos-daily-management-recap-delete";

    public static final String CASH_DRAWER_CHECKOUT_CLERK_READ = "cash-drawer-checkout-clerk-read";
    public static final String CASH_DRAWER_CHECKOUT_CLERK_UPDATE = "cash-drawer-checkout-clerk-update";
    public static final String CASH_DRAWER_CHECKOUT_CLERK_CREATE = "cash-drawer-checkout-clerk-create";
    public static final String CASH_DRAWER_CHECKOUT_CLERK_DELETE = "cash-drawer-checkout-clerk-delete";

    public static final String SERVER_CASH_DRAWER_READ = "cashdrawers/get";

    private Permission() {
        throw new IllegalStateException();
    }
}
