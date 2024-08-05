package com.amazonaws.saas.eks.exception;

import com.amazonaws.saas.eks.error.DependencyViolationErrorItem;
import lombok.Getter;

import java.util.List;

public class ProductCategoryInvalidDeleteException extends RuntimeException {
    @Getter
    private String entity;

    @Getter
    private List<DependencyViolationErrorItem> items;

    public ProductCategoryInvalidDeleteException(String categoryId, String level, String storeId, String reason) {
        super(String.format("The product category '%s' cannot be deleted because: '%s'. Level: %s. StoreId: %s.",
                categoryId, reason, level, storeId));
    }

    public ProductCategoryInvalidDeleteException(String categoryId, String level, String storeId,
                                                 String entity, List<DependencyViolationErrorItem> items) {
        super(String.format("The product category '%s' cannot be deleted because has elements of type '%s' " +
                        "associated to. Level: %s. StoreId: %s.",
                categoryId, entity, level, storeId));
        this.entity = entity;
        this.items = items;
    }
}
