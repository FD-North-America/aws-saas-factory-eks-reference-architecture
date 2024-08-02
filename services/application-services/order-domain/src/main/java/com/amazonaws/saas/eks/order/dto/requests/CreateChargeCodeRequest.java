package com.amazonaws.saas.eks.order.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateChargeCodeRequest {
    @NotNull
    private String orderId;

    @NotBlank
    @Size(max = 10)
    private String code;

    @NotNull
    private BigDecimal amount;
}
