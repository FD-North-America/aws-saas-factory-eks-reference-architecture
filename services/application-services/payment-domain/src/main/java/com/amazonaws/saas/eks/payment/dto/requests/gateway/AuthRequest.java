package com.amazonaws.saas.eks.payment.dto.requests.gateway;

import lombok.Data;

@Data
public class AuthRequest {
    private String merchId;
    private String amount;
    private String expiry;
    private String account;
    private String postal;
    private String ecomind;
    private String orderId;
}
