package com.amazonaws.saas.eks.payment.model;

public class Permission {
    public static final String POS_CREATE = "pos-daily-management-recap-create";
    public static final String POS_READ = "pos-daily-management-recap-read";
    public static final String POS_UPDATE = "pos-daily-management-recap-update";
    public static final String POS_DELETE = "pos-daily-management-recap-delete";
    public static final String POS_SERVER_UPDATE = "payments/update";
    public static final String POS_SERVER_READ = "payments/get";

    private Permission() {
        throw new IllegalStateException("Utility class");
    }
}
