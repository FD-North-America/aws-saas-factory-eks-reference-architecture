package com.amazonaws.saas.eks.settings.model.enums;

import lombok.Getter;

@Getter
public enum EntityType {
    POS("POS"),
    INVENTORY("INVENTORY"),
    SALES_TAX("SALES_TAX"),
    PURCHASING("PURCHASING"),
    ACCOUNTS_RECEIVABLE("ACCOUNTS_RECEIVABLE"),
    REASON_CODES("REASON_CODES");

    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
