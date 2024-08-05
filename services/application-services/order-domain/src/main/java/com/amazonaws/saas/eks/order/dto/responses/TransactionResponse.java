package com.amazonaws.saas.eks.order.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private String retRef;

    private Date date;

    private String type;

    private String ccType;

    private String ccLastDigits;

    private BigDecimal amount;

    private String checkNumber;

    private String invoiceNumber;

    private String paymentType;
}
