package com.amazonaws.saas.eks.product.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductSearchResponse {
    private List<Product> products;

    private long count;
}
