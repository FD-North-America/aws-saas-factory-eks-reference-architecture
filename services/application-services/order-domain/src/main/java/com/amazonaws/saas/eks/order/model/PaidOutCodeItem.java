package com.amazonaws.saas.eks.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaidOutCodeItem {
    private String id;

    private String type;

    private BigDecimal amount;

    private String code;
}
