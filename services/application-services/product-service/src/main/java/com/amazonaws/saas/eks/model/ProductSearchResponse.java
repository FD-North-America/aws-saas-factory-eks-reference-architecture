package com.amazonaws.saas.eks.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ProductSearchResponse {
    @Getter
    @Setter
    private List<Product> products;

    @Getter
    @Setter
    private long count;
}
