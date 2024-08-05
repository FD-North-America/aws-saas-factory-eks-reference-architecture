package com.amazonaws.saas.eks.processors.lineitems.products;

import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductProcessorParams {
    private List<LineItem> lineItems;
    private LineItemRequest request;
    private PricingResponse pricing;
}
