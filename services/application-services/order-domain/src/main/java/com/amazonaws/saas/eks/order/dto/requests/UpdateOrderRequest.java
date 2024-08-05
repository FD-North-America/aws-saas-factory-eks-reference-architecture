package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.dto.requests.reasoncodes.ReasonCodeItemRequest;
import com.amazonaws.saas.eks.order.model.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateOrderRequest {
    @ValueOfEnum(enumClass = OrderStatus.class)
    private String status;

    private BigDecimal cashPaymentAmount;

    private BigDecimal creditPaymentAmount;

    private List<TransactionRequest> transactions;

    private List<ReasonCodeItemRequest> reasonCodes;

    private String linkedOrderId;

    private boolean delivered;
}
