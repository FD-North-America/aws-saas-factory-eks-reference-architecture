package com.amazonaws.saas.eks.dto.requests.orders;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {

    @Getter
    @Setter
    private List<LineItemRequest> lineItems = new ArrayList<>();

    @NotNull
    @Getter
    @Setter
    private String cashDrawerId;
}
