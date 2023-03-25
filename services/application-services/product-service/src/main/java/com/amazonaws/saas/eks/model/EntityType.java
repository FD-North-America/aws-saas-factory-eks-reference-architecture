package com.amazonaws.saas.eks.model;

import lombok.Getter;

public enum EntityType {
    CATEGORIES("CATEGORIES"),
    PRODUCTS("PRODUCTS"),
    VENDORS("VENDORS"),
    UOM("UOM"),
    VOLUME_PRICING("VOLUMEPRICING");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
