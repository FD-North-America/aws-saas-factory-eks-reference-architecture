package com.amazonaws.saas.eks.order.model.enums;

import lombok.Getter;

public enum EntityType {
    ORDERS("ORDERS"),
    DISCOUNTS("DISCOUNTS"),
    PAIDOUTCODES("PAIDOUTCODES"),
    CATEGORY_SALES("CATEGORYSALES"),
    DELIVERIES("DELIVERIES"),
    CHARGE_CODES("CHARGECODES"),
    TAXES("TAXES"),
    PRODUCT_ORDERS("PRODUCTORDERS");

    @Getter
    private final String label;

    EntityType(String label) {
        this.label = label;
    }
}
