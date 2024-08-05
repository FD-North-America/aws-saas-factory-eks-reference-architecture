package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.dto.requests.reasoncodes.ReasonCodeItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private List<LineItemRequest> lineItems = new ArrayList<>();
    private List<PaidOutCodeRequest> paidOutCodes = new ArrayList<>();
    private List<ReasonCodeItemRequest> reasonCodes = new ArrayList<>();
    private String linkedOrderId;
}
