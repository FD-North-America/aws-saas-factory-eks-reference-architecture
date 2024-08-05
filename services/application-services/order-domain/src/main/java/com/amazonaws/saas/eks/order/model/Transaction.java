package com.amazonaws.saas.eks.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class Transaction implements Serializable {
    private String retRef;

    private Date date;

    private String type;

    private String ccType;

    private String ccLastDigits;

    private BigDecimal amount;

    private String checkNumber;

    private String paymentType;
}
