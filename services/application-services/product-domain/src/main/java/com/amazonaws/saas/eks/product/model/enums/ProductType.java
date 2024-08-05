package com.amazonaws.saas.eks.product.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum ProductType {
    NORMAL("Normal"),
    GENERIC("Generic");

    private final String label;

    ProductType(String label) {
        this.label = label;
    }

    private static final Map<String, ProductType> BY_LABEL = new HashMap<>();

    static {
        for (ProductType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static ProductType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
