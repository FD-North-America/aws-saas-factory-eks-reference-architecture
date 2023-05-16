package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class ReadConfirmationRequest {
    @NotEmpty
    @Getter
    @Setter
    private String prompt;

    @NonNull
    @Getter
    @Setter
    private Boolean beep;
}
