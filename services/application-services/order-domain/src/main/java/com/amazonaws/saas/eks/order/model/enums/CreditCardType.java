package com.amazonaws.saas.eks.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum CreditCardType {
    MASTERCARD("MasterCard"),
    VISA("Visa"),
    DISCOVER("Discover"),
    AMERICAN_EXPRESS("AmericanExpress");

    private final String label;

    CreditCardType(String label) {
        this.label = label;
    }

    private static final Map<String, CreditCardType> BY_LABEL = new HashMap<>();

    static {
        for (CreditCardType t : values()) {
            BY_LABEL.put(t.label, t);
        }
    }

    @JsonCreator
    public static CreditCardType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
