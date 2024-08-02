package com.amazonaws.saas.eks.product.dto.requests.category;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ListCategoriesRequestParams implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String filter;

    @Getter
    @Setter
    private String level;
}
