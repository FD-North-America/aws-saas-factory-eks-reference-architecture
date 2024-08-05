package com.amazonaws.saas.eks.cashdrawer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum CashDrawerStatus {

    ACTIVE("Active"),
    CHECKED("Checked"),
    CLEARED("Cleared"),
    DRAFT("Draft"),
    DELETED("Deleted"),
    INACTIVE("Inactive");

    private final String label;

    CashDrawerStatus(String label) {
        this.label = label;
    }

    private static final Map<String, CashDrawerStatus> BY_LABEL = new HashMap<>();

    static {
        for (CashDrawerStatus c : values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    @JsonCreator
    public static CashDrawerStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
