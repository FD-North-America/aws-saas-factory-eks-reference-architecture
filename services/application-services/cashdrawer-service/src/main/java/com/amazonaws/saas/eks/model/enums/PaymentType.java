package com.amazonaws.saas.eks.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum PaymentType {
    CASH("Cash"),
    CARD("Card"),
    CHECK("Check"),
    MULTIPLE("Multiple");

    private final String label;

    PaymentType(String label) {
        this.label = label;
    }

    private static final Map<String, PaymentType> BY_LABEL = new HashMap<>();

    static {
        for (PaymentType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static PaymentType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
