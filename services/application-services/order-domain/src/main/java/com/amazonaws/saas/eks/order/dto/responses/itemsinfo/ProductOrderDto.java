package com.amazonaws.saas.eks.order.dto.responses.itemsinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductOrderDto {
    private String orderId;

    private String orderNumber;

    private String customerName;

    private String vendorName;

    private Integer quantity;

    private String status;
}
