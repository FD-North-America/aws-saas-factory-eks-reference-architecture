package com.amazonaws.saas.eks.dto.responses.product;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListProductResponse {
    @Getter
    @Setter
    private List<ProductResponse> products = new ArrayList<>();

    @Getter
    @Setter
    private long count;
}
