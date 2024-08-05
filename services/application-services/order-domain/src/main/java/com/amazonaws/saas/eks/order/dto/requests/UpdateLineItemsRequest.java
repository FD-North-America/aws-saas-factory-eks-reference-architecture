package com.amazonaws.saas.eks.order.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UpdateLineItemsRequest {
    @NotNull
    @Getter
    @Setter
    private List<LineItemRequest> lineItems = new ArrayList<>();
}
