package com.amazonaws.saas.eks.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum ProductTaxable {
    TAXABLE("Taxable"),
    EXEMPT("Exempt");

    private final String label;

    ProductTaxable(String label) {
        this.label = label;
    }

    private static final Map<String, ProductTaxable> BY_LABEL = new HashMap<>();

    static {
        for (ProductTaxable e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static ProductTaxable valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
