package com.amazonaws.saas.eks.product.dto.requests.product;

import lombok.Getter;
import lombok.Setter;

public class UpdateProductCountRequest {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private Float count;
}
