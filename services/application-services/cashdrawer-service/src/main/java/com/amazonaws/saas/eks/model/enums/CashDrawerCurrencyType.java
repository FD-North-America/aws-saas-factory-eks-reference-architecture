package com.amazonaws.saas.eks.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum CashDrawerCurrencyType {
    PENNY("Penny"),
    NICKEL("Nickel"),
    DIME("Dime"),
    QUARTER("Quarter"),
    DOLLAR("Dollar"),
    TWO("Two"),
    FIVE("Five"),
    TEN("Ten"),
    TWENTY("Twenty"),
    FIFTY("Fifty"),
    HUNDRED("Hundred");

    private final String label;

    CashDrawerCurrencyType(String label) {
        this.label = label;
    }

    private static final Map<String, CashDrawerCurrencyType> BY_LABEL = new HashMap<>();

    static {
        for (CashDrawerCurrencyType c : values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    @JsonCreator
    public static CashDrawerCurrencyType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
