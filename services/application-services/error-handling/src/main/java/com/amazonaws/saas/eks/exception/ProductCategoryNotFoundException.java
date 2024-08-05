package com.amazonaws.saas.eks.exception;

public class ProductCategoryNotFoundException extends RuntimeException {
    public ProductCategoryNotFoundException(String categoryId, String storeId) {
        super(String.format("The product category '%s' doesn't exist. StoreId: %s.",
                categoryId, storeId));
    }

    public ProductCategoryNotFoundException(String categoryId, String level, String storeId) {
        super(String.format("The product category '%s' doesn't exist. Level: %s. StoreId: %s.",
                categoryId, level, storeId));
    }
}
