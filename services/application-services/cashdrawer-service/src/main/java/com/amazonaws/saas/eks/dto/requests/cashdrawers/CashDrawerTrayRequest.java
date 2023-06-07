package com.amazonaws.saas.eks.dto.requests.cashdrawers;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.enums.CashDrawerCurrencyType;
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
