package com.amazonaws.saas.eks.processors.lineitems.discounts;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.order.model.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DiscountProcessorParams {
    private Order order;
    private List<LineItem> lineItems;
    private LineItemRequest request;
    private String tenantId;
    private CashDrawerResponse cashDrawerResponse;
}
