package com.amazonaws.saas.eks.payment.dto.requests;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ReadManualRequest {
    @NonNull
    @Getter
    @Setter
    private Boolean beep;

    @NonNull
    @Getter
    @Setter
    private Boolean includeSignature;

    @NonNull
    @Getter
    @Setter
    private Boolean includeExpirationDate;
}
