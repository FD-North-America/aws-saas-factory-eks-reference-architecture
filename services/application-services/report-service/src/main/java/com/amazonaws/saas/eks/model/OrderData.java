package com.amazonaws.saas.eks.model;

import com.amazonaws.saas.eks.order.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OrderData {
    private List<Order> orders = new ArrayList<>();
    private long count;
    private Map<String, BigDecimal> aggregationMap = new HashMap<>();
}
