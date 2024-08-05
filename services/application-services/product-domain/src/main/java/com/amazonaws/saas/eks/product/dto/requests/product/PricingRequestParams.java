package com.amazonaws.saas.eks.product.dto.requests.product;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PricingRequestParams {
    @Getter
    @Setter
    private List<ProductPricingRequest> productPricingRequests = new ArrayList<>();
}
