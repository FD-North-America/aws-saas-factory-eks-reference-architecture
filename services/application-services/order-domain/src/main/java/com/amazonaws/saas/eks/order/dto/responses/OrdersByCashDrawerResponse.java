package com.amazonaws.saas.eks.order.dto.responses;

import com.amazonaws.saas.eks.order.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrdersByCashDrawerResponse {
    private List<Order> orders;

    private long count;
}
