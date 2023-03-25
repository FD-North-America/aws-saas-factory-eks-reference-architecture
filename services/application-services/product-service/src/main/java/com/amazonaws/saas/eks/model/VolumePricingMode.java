package com.amazonaws.saas.eks.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum VolumePricingMode {
    FLAT_RATE("FlatRate"),
    DISCOUNT_PERCENTAGE("DiscountPercentage");

    private final String label;

    VolumePricingMode(String label) {
        this.label = label;
    }

    private static final Map<String, VolumePricingMode> BY_LABEL = new HashMap<>();

    static {
        for (VolumePricingMode e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static VolumePricingMode valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
