package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum DiscountType {
    PERCENTAGE("Percentage"),
    DOLLAR("Dollar");

    private final String label;

    DiscountType(String label) {
        this.label = label;
    }

    private static final Map<String, DiscountType> BY_LABEL = new HashMap<>();

    static {
        for (DiscountType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static DiscountType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
