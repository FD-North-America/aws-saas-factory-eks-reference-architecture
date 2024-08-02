package com.amazonaws.saas.eks.payment.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ConnectRequest {
    @Getter
    @Setter
    private String merchantId;

    @Getter
    @Setter
    private String hsn;

    @Getter
    @Setter
    private Boolean force;
}
