package com.amazonaws.saas.eks.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum EntityStatus {
    ACTIVE("Active"),
    DELETED("Deleted");

    private final String label;

    EntityStatus(String label) {
        this.label = label;
    }

    private static final Map<String, EntityStatus> BY_LABEL = new HashMap<>();

    static {
        for (EntityStatus s : values()) {
            BY_LABEL.put(s.label, s);
        }
    }

    @JsonCreator
    public static EntityStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
