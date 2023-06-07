package com.amazonaws.saas.eks.dto.requests.orders;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.enums.CreditCardType;
import com.amazonaws.saas.eks.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class TransactionRequest {
    @ValueOfEnum(enumClass = PaymentType.class)
    @Getter
    @Setter
    private String type;

    @ValueOfEnum(enumClass = CreditCardType.class)
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
    private String retRef;
}
