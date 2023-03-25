package com.amazonaws.saas.eks.model;

import lombok.Getter;

public enum CategoryLevel {
    CATEGORY("Category"),
    SUB_CATEGORY("SubCategory"),
    GROUP("Group");

    @Getter
    private final String label;

    CategoryLevel(String label) {
        this.label = label;
    }
}
