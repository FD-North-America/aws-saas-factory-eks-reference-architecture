package com.amazonaws.saas.eks.dto.requests.orders;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class UpdateOrderRequest {
    @ValueOfEnum(enumClass = OrderStatus.class)
    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private BigDecimal cashPaymentAmount;

    @Getter
    @Setter
    private BigDecimal creditPaymentAmount;

    @Getter
    @Setter
    private List<TransactionRequest> transactions;
}
