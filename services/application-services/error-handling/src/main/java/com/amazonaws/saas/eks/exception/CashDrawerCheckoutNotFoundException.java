package com.amazonaws.saas.eks.exception;

public class CashDrawerCheckoutNotFoundException extends RuntimeException {
    public CashDrawerCheckoutNotFoundException(String cashDrawerCheckoutId, String tenantId) {
        super(String.format("The cash drawer checkout '%s' doesn't exist. TenantId: %s.", cashDrawerCheckoutId,
                tenantId));
    }
}
