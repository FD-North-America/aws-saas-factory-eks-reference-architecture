package com.amazonaws.saas.eks.order.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaidOutCodeResponse {
    private String id;

    private String type;

    private BigDecimal amount;

    private String code;
}
