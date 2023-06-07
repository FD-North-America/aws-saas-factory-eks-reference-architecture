package com.amazonaws.saas.eks.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

public class CashDrawerTray implements Serializable {

    private static final long serialVersionUID = 123456L;

    @Getter
    @Setter
    private String currency;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private Integer rolls;
}
