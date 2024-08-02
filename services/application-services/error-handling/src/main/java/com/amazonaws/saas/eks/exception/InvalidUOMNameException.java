package com.amazonaws.saas.eks.exception;

public class InvalidUOMNameException extends RuntimeException {
    public InvalidUOMNameException(String tenantId, String uomName) {
        super(String.format("Invalid UOM name %s for TenantId %s", uomName, tenantId));
    }
}
