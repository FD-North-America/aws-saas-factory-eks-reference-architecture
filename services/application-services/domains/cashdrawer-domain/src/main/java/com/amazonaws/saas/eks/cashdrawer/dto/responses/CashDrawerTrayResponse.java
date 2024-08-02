package com.amazonaws.saas.eks.cashdrawer.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class CashDrawerTrayResponse {
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
