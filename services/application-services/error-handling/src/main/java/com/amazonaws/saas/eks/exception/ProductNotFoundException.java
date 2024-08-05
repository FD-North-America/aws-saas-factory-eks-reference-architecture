package com.amazonaws.saas.eks.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId, String storeId) {
        super(String.format("The product '%s' doesn't exist. StoreId: %s.",
                productId, storeId));
    }
}
