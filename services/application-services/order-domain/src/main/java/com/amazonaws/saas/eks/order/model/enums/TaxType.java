package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum TaxType {
    TAXABLE("Taxable"),
    RESELLER("Reseller"),
    EXEMPT("Exempt");

    private final String label;

    TaxType(String label) {
        this.label = label;
    }

    private static final Map<String, TaxType> BY_LABEL = new HashMap<>();

    static {
        for (TaxType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static TaxType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
