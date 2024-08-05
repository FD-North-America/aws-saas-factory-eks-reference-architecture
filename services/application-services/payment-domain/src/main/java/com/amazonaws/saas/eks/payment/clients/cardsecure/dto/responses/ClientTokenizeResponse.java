package com.amazonaws.saas.eks.payment.clients.cardsecure.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClientTokenizeResponse {
    private String message;
    private String token;
    @JsonProperty("errorcode")
    private String errorCode;
}
