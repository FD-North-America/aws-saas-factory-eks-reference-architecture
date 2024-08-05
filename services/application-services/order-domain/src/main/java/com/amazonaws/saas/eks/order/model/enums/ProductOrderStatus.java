package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum ProductOrderStatus {
    ON_ORDER("OnOrder"),
    COMMITTED("Committed");

    private final String label;

    ProductOrderStatus(String label) {
        this.label = label;
    }

    private static final Map<String, ProductOrderStatus> BY_LABEL = new HashMap<>();

    static {
        for (ProductOrderStatus t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static ProductOrderStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
