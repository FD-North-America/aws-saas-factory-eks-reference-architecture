package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class TransactionRequest {
    @ValueOfEnum(enumClass = PaymentType.class)
    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private String checkNumber;
}
