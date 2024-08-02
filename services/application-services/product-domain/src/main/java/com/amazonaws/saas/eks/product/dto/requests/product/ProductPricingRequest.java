package com.amazonaws.saas.eks.product.dto.requests.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class ProductPricingRequest {
    private String productId;
    private String uomId;
    private Integer quantity;
    private String barcode;
    private String sku;
}
