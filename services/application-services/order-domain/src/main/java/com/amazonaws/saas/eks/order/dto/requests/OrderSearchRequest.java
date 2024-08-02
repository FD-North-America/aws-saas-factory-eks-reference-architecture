package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.OrderType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchRequest {
    private Integer from;
    private Integer size;
    private String filter;
    @ValueOfEnum(enumClass = OrderType.class)
    private String type = OrderType.SALE.toString();
    private String sortBy;
}
