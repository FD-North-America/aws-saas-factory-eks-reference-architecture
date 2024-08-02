package com.amazonaws.saas.eks.exception;

public class InvalidUOMProductIdException extends RuntimeException {
    public InvalidUOMProductIdException(String tenantId, String productId) {
        super(String.format("Invalid UOM productId: %s, for TenantId %s", productId, tenantId));
    }
}
