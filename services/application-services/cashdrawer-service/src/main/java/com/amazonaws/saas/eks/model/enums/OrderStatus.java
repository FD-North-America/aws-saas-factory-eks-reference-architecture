package com.amazonaws.saas.eks.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {
    PENDING("Pending"),
    PAID("Paid"),
    RETURNED("Returned"),
    DELETED("Deleted");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    private static final Map<String, OrderStatus> BY_LABEL = new HashMap<>();

    static {
        for (OrderStatus e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static OrderStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
