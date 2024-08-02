package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum PaidOutCodeType {
    ONLINE_REDEEM("OnlineRedeem"),
    INSTANT_REDEEM("InstantRedeem"),
    MISC("Misc");

    private final String label;

    PaidOutCodeType(String label) {
        this.label = label;
    }

    private static final Map<String, PaidOutCodeType> BY_LABEL = new HashMap<>();

    static {
        for (PaidOutCodeType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    @JsonCreator
    public static PaidOutCodeType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
