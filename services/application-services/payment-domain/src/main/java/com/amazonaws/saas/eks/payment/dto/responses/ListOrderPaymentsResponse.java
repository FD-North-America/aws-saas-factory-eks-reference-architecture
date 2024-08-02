package com.amazonaws.saas.eks.payment.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListOrderPaymentsResponse {
    @Getter
    @Setter
    private List<OrderPaymentResponse> payments = new ArrayList<>();
}
