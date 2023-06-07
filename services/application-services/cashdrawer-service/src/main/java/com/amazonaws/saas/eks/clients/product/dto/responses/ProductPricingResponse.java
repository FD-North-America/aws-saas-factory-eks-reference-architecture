package com.amazonaws.saas.eks.clients.product.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class ProductPricingResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String taxable;

    @Getter
    @Setter
    private BigDecimal retailPrice;

    @Getter
    @Setter
    private Integer quantity;

    @Getter
    @Setter
    private String sku;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private VolumePricingResponse volumePricing;
}
