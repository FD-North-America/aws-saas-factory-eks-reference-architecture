package com.amazonaws.saas.eks.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class ConnectRequest {
    @NotEmpty
    @Getter
    @Setter
    private String merchantId;

    @NotEmpty
    @Getter
    @Setter
    private String hsn;

    @Getter
    @Setter
    private Boolean force;
}
