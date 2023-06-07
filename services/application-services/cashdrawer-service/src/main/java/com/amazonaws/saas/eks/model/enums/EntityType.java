package com.amazonaws.saas.eks.model.enums;

import lombok.Getter;

public enum EntityType {
    ORDERS("ORDERS"),
    DISCOUNTS("DISCOUNTS"),
    CASHDRAWERS("CASHDRAWERS"),
    CASHDRAWERCHECKOUTS("CASHDRAWERCHECKOUTS");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
