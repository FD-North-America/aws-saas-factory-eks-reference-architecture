package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum LineItemType {
    PRODUCT("Product"),
    GENERIC("Generic"),
    DISCOUNT("Discount");

    private final String label;

    LineItemType(String label) {
        this.label = label;
    }

    private static final Map<String, LineItemType> BY_LABEL = new HashMap<>();

    static {
        for (LineItemType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static LineItemType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
