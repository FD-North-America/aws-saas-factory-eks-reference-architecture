package com.amazonaws.saas.eks.order.model.search;

import com.amazonaws.saas.eks.order.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderSearchResponse {
    private List<Order> orders = new ArrayList<>();
    private long count;
}
