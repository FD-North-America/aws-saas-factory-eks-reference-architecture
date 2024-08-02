package com.amazonaws.saas.eks.exception;

public class CashDrawerNotFoundException extends RuntimeException {
    public CashDrawerNotFoundException(String cashDrawerId, String tenantId) {
        super(String.format("The cash drawer '%s' doesn't exist. TenantId: %s.", cashDrawerId, tenantId));
    }
}
