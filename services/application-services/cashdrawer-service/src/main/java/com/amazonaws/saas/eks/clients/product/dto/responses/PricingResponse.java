package com.amazonaws.saas.eks.clients.product.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PricingResponse {
    @Getter
    @Setter
    private Map<String, ProductPricingResponse> productPricing = new HashMap<>();

    @Getter
    @Setter
    private BigDecimal taxRate;
}
