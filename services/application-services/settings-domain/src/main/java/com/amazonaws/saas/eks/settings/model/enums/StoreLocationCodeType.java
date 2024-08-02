package com.amazonaws.saas.eks.settings.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum StoreLocationCodeType {
    RETAIL("Retail"),
    YARD("Yard"),
    WAREHOUSE("Warehouse");

    private final String label;

    StoreLocationCodeType(String label) {
        this.label = label;
    }

    private static final Map<String, StoreLocationCodeType> BY_LABEL = new HashMap<>();

    static {
        for (StoreLocationCodeType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static StoreLocationCodeType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
