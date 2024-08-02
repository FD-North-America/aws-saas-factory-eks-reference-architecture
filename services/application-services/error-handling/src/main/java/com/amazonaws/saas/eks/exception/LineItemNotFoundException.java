package com.amazonaws.saas.eks.exception;

public class LineItemNotFoundException extends RuntimeException {
    public LineItemNotFoundException(String tenantId, String orderId, String lineItemId) {
        super(String.format("The line item '%s' for Order '%s' doesn't exist. TenantId: %s",
                lineItemId, orderId, tenantId));
    }
}
