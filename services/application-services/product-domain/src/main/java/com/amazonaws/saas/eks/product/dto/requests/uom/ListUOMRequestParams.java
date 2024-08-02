package com.amazonaws.saas.eks.product.dto.requests.uom;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class ListUOMRequestParams {
    @NotEmpty
    @Getter
    @Setter
    private String productId;
}
