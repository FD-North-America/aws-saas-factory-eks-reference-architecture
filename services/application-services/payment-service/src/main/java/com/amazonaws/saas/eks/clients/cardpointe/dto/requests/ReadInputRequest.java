package com.amazonaws.saas.eks.clients.cardpointe.dto.requests;

import lombok.Getter;
import lombok.Setter;

public class ReadInputRequest {
    @Getter
    @Setter
    private String merchantId;

    @Getter
    @Setter
    private String hsn;

    @Getter
    @Setter
    private String prompt;

    @Getter
    @Setter
    private Boolean beep;

    @Getter
    @Setter
    private String format;
}
