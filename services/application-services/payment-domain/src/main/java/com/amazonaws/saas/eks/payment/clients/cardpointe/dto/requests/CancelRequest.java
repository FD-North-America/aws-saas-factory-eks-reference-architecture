package com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class CancelRequest {
    @Getter
    @Setter
    private String merchantId;

    @Getter
    @Setter
    private String hsn;
}
