package com.amazonaws.saas.eks.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class DeliveryAddressLine implements Serializable {
    private String address;
    private String taxCode;
    private BigDecimal tax;
}
