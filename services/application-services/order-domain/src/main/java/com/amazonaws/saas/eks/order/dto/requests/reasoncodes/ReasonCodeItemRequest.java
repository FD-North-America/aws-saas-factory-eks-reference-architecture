package com.amazonaws.saas.eks.order.dto.requests.reasoncodes;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReasonCodeItemRequest {
    @NotBlank
    @NotNull
    private String code;
}
