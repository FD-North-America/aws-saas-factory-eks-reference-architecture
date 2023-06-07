package com.amazonaws.saas.eks.clients.product.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PricingRequestParams {
    @Getter
    @Setter
    private List<ProductPricingRequest> productPricingRequests = new ArrayList<>();
}
