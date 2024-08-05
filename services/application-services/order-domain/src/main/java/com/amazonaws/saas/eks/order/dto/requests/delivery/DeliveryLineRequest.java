package com.amazonaws.saas.eks.order.dto.requests.delivery;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DeliveryLineRequest {
    private String address;
    private String taxCode;
    private BigDecimal tax;
}
