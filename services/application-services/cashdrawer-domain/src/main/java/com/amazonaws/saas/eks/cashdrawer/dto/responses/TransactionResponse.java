package com.amazonaws.saas.eks.cashdrawer.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionResponse {
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

    @Getter
    @Setter
    private String invoiceNumber;
}
