package com.amazonaws.saas.eks.order.dto.responses.itemsinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemInfo {
    private String productId;

    private List<ProductOrderDto> productOrders;

    private ProductOrdersDetails productOrdersDetails;
}
