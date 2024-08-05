package com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ReadManualRequest {
    @Getter
    @Setter
    private String merchantId;

    @Getter
    @Setter
    private String hsn;

    @Getter
    @Setter
    private Boolean beep;

    @Getter
    @Setter
    private Boolean includeSignature;

    @Getter
    @Setter
    private Boolean includeExpirationDate;
}
