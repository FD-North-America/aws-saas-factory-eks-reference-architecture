package com.amazonaws.saas.eks.customer.model.enums;

import lombok.Getter;

public enum EntityType {
    CUSTOMERS("CUSTOMERS"),
    ACCOUNTS("ACCOUNTS"),
    COUNTER("COUNTER"),
    CERTIFICATES("CERTIFICATES");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
