package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum OrderType {
    SALE("Sale"),
    QUOTE("Quote"),
    INVOICE("Invoice");

    private final String label;

    OrderType(String label) {
        this.label = label;
    }

    private static final Map<String, OrderType> BY_LABEL = new HashMap<>();

    static {
        for (OrderType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static OrderType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
