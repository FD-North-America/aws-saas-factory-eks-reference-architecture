package com.amazonaws.saas.eks.payment.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPaymentResponse {
    private String orderNumber;
    private String status;
    private AuthResponse response;
    private String responseDate;
}
