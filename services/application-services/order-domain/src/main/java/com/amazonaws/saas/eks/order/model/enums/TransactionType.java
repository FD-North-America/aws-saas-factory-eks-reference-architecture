package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {
    TENDERED("Tendered"),
    CHANGE("Change"),
    PAID_OUT("PaidOut");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    private static final Map<String, TransactionType> BY_LABEL = new HashMap<>();

    static {
        for (TransactionType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static TransactionType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
