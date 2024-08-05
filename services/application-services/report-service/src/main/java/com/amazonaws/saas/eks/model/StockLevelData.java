package com.amazonaws.saas.eks.model;

import com.amazonaws.saas.eks.product.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class StockLevelData {
    @Getter
    @Setter
    private List<Product> products = new ArrayList<>();

    @Getter
    @Setter
    private long count;
}
