package com.amazonaws.saas.eks.order.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListOrdersResponse {
    private List<OrderRowResponse> orders = new ArrayList<>();
    private long count;
}
