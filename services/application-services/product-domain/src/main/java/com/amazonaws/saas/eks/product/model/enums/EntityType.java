package com.amazonaws.saas.eks.product.model.enums;

import lombok.Getter;

public enum EntityType {
    CATEGORIES("CATEGORIES"),
    PRODUCTS("PRODUCTS"),
    VENDORS("VENDORS"),
    UOM("UOM"),
    VOLUME_PRICING("VOLUMEPRICING"),
    SALES_HISTORY("SALESHISTORY"),
    COUNTER("COUNTER");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
