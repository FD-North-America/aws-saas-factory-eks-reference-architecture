package com.amazonaws.saas.eks.cashdrawer.dto.requests;

import com.amazonaws.saas.eks.cashdrawer.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerCurrencyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class CashDrawerTrayRequest {
    @ValueOfEnum(enumClass = CashDrawerCurrencyType.class)
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
