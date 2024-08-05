package com.amazonaws.saas.eks.exception;

import com.amazonaws.saas.eks.error.DependencyViolationErrorItem;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidDeleteException extends RuntimeException {
    private final String entity;
    private final transient List<DependencyViolationErrorItem> items;

    public InvalidDeleteException(String id, String entity, List<DependencyViolationErrorItem> items) {
        super(String.format("The element with ID '%s' cannot be deleted because has elements of type '%s' " +
                        "associated to.", id, entity));
        this.entity = entity;
        this.items = items;
    }
}
