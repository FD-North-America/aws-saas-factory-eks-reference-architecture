package com.amazonaws.saas.eks.settings.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum SequenceNumberType {
    ORDER("Order"),
    INVOICE("Invoice"),
    QUOTE("Quote"),
    PAID_OUT("PaidOut");

    private final String label;

    SequenceNumberType(String label) {
        this.label = label;
    }

    private static final Map<String, SequenceNumberType> BY_LABEL = new HashMap<>();

    static {
        for (SequenceNumberType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static SequenceNumberType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
