package com.amazonaws.saas.eks.payment.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ListTerminalsRequest {
    @Getter
    @Setter
    private String merchantId;
}
