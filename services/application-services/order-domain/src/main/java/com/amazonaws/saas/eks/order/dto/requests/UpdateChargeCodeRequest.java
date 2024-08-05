package com.amazonaws.saas.eks.order.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateChargeCodeRequest {
    @Size(max = 10)
    private String code;

    private BigDecimal amount;
}
