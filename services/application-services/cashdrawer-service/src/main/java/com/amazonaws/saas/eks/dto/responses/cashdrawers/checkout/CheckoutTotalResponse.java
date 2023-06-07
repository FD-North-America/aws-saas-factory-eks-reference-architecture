package com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class CheckoutTotalResponse {
    @Getter
    @Setter
    private BigDecimal sales = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal paidOut = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal balance = BigDecimal.ZERO;
}
