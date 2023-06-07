package com.amazonaws.saas.eks.dto.responses.cashdrawers;

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
