package com.amazonaws.saas.eks.order.dto.responses.itemsinfo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductOrdersDetails {
    private BigDecimal cost;

    private BigDecimal retailPrice;

    private Integer quantityOnHand;

    private String pricingUom;

    private Integer committedQty;

    private Integer availableQty;

    private Integer onOrderQty;

    private String quantityUom;
}
