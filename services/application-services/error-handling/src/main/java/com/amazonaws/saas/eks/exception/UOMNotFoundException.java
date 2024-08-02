package com.amazonaws.saas.eks.exception;

public class UOMNotFoundException extends RuntimeException {
    public UOMNotFoundException(String uomId, String tenantId, String storeId) {
        super(String.format("The product UOM '%s' doesn't exist. TenantId: %s. StoreId: %s.",
                uomId, tenantId, storeId));
    }

    public UOMNotFoundException(String uomId, String tenantId) {
        super(String.format("The product UOM '%s' doesn't exist. TenantId: %s", uomId, tenantId));
    }
}
