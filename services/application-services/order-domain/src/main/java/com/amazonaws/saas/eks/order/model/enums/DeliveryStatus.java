package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum DeliveryStatus {
    ACTIVE("Active"),
    DELIVERED("Delivered"),
    DELETED("Deleted");

    private final String label;
    DeliveryStatus(String label) {
        this.label = label;
    }

    private  static final Map<String, DeliveryStatus> BY_LABEL = new HashMap<>();

    static {
        for (DeliveryStatus s : values()) {
            BY_LABEL.put(s.label, s);
        }
    }

    @JsonCreator
    public static DeliveryStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
