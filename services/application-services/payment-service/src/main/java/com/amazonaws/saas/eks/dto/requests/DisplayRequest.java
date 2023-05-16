package com.amazonaws.saas.eks.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class DisplayRequest {
    @NotEmpty
    @Getter
    @Setter
    private String text;
}
