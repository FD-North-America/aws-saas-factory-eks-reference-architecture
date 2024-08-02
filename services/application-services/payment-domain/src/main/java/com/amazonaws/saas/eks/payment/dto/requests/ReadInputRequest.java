package com.amazonaws.saas.eks.payment.dto.requests;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class ReadInputRequest {
    @NotEmpty
    @Getter
    @Setter
    private String prompt;

    @NonNull
    @Getter
    @Setter
    private Boolean beep;

    @NotEmpty
    @Getter
    @Setter
    private String format;
}
