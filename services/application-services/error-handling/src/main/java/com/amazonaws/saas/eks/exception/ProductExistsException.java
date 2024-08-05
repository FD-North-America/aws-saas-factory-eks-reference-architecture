package com.amazonaws.saas.eks.exception;

public class ProductExistsException extends RuntimeException {
    public ProductExistsException(String productId, String tenantId, String storeId) {
        super(String.format("The product '%s' already exists. TenantId: %s. StoreId: %s.",
                productId, tenantId, storeId));
    }

    public ProductExistsException(String productId, String detail, String tenantId, String storeId) {
        super(String.format("The product '%s' already exists. %s. TenantId: %s. StoreId: %s.",
                productId, detail, tenantId, storeId));
    }
}
