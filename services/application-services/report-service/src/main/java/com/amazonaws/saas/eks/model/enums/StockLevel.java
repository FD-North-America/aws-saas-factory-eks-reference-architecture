package com.amazonaws.saas.eks.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum StockLevel {
    POSITIVE("Positive"),
    ZERO("Zero"),
    NEGATIVE("Negative"),
    ALL("All");

    private final String label;

    StockLevel(String label) {
        this.label = label;
    }

    private static final Map<String, StockLevel> BY_LABEL = new HashMap<>();

    static {
        for (StockLevel l : values()) {
            BY_LABEL.put(l.label, l);
        }
    }

    @JsonCreator
    public static StockLevel valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
