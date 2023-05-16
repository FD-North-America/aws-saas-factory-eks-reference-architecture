package com.amazonaws.saas.eks.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthManualRequest {
    private String merchantId;

    private String hsn;

    private String amount;

    private Boolean includeSignature;

    private Boolean includeAmountDisplay;

    private Boolean beep;

    private Boolean includeAVS;

    private Boolean includeCVV;

    private Boolean capture;

    private Boolean gzipSignature;

    private String orderId;

    private String clearDisplayDelay;
}
