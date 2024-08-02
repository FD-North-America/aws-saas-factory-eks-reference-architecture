package com.amazonaws.saas.eks.cashdrawer.model.enums;

import lombok.Getter;

public enum EntityType {
    CASHDRAWERS("CASHDRAWERS"),
    CASHDRAWERCHECKOUTS("CASHDRAWERCHECKOUTS"),
    WORKSTATIONS("WORKSTATIONS"),
    COUNTER("COUNTER");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
