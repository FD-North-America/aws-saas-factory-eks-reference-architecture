package com.amazonaws.saas.eks.order.dto.responses.delivery;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DeliveryLineResponse {
    private String address;
    private String taxCode;
    private BigDecimal tax;
}
