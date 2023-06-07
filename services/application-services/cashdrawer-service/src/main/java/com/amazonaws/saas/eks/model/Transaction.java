package com.amazonaws.saas.eks.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 2753684762296062149L;

    @Getter
    @Setter
    private String retRef;

    @Getter
    @Setter
    private Date date;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String ccType;

    @Getter
    @Setter
    private String ccLastDigits;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private String checkNumber;
}
