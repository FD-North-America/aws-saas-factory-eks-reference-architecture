package com.amazonaws.saas.eks.clients.product.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

public class ProductPricingRequest {
    @Getter
    @Setter
    private String productId;

    @Getter
    @Setter
    private String uomId;

    @Min(value = 0)
    @Getter
    @Setter
    private Integer quantity;

    @Getter
    @Setter
    private String barcode;
}
