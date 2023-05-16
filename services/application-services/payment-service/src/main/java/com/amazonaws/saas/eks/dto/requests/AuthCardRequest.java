package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AuthCardRequest {
    private String amount;

    private boolean includeSignature;

    private boolean includeAmountDisplay;

    private boolean beep;

    private String aid;

    private boolean includeAVS;

    private boolean capture;

    private String orderId;

    private Map<String, String> userFields;

    private int clearDisplayDelay;
}
