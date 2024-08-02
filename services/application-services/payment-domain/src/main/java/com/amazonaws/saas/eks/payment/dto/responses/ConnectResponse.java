package com.amazonaws.saas.eks.payment.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ConnectResponse {
    @Getter
    @Setter
    private String session;

    @Getter
    @Setter
    private Date expiryDate;
}
