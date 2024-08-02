package com.amazonaws.saas.eks.order.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportLineItemsRequest {
    private String orderId;
    private Boolean useQuotePricing;
    private List<LineItemRequest> lineItems;
}
