package com.amazonaws.saas.eks.payment.clients.cardconnect.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClientAuthRequest {
    @JsonProperty("merchid")
    private String merchId;
    private String amount;
    private String expiry;
    private String account;
    private String postal;
    private String ecomind = "T";
    @JsonProperty("orderid")
    private String orderId;
    private String capture = "y";
}
