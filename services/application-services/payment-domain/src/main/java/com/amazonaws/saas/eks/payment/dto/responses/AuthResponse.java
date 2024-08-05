package com.amazonaws.saas.eks.payment.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String name;
    @JsonProperty("retref")
    private String retRef;
    private String amount;
    @JsonProperty("respcode")
    private String respCode;
    @JsonProperty("resptext")
    private String respText;
    @JsonProperty("respstat")
    private String respStat;
}
