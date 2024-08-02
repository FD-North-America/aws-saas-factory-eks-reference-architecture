package com.amazonaws.saas.eks.product.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum VendorStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String label;

    VendorStatus(String label) {
        this.label = label;
    }

    private static final Map<String, VendorStatus> BY_LABEL = new HashMap<>();

    static {
        for (VendorStatus e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static VendorStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
