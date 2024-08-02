package com.amazonaws.saas.eks.order.model;

public class Permission {
    public static final String SERVER_ORDER_READ = "orders/get";

    private Permission() {
        throw new IllegalStateException();
    }
}
