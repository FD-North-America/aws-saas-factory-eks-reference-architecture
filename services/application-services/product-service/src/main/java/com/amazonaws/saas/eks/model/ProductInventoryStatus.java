package com.amazonaws.saas.eks.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum ProductInventoryStatus {
    ACTIVE("Active"),
    DISCONTINUED("Discontinued"),
    INACTIVE("Inactive");

    private final String label;

    ProductInventoryStatus(String label) {
        this.label = label;
    }

    private static final Map<String, ProductInventoryStatus> BY_LABEL = new HashMap<>();

    static {
        for (ProductInventoryStatus e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static ProductInventoryStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
