package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRecapAmounts {
    private BigDecimal sales = BigDecimal.ZERO;
    private BigDecimal paidOut = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
}
