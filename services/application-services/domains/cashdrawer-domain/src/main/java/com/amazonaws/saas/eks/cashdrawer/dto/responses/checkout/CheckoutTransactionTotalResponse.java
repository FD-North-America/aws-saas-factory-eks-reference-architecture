package com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout;

import lombok.Getter;
import lombok.Setter;

public class CheckoutTransactionTotalResponse {
    @Getter
    @Setter
    private CheckoutTotalResponse cash;

    @Getter
    @Setter
    private CheckoutTotalResponse card;

    @Getter
    @Setter
    private CheckoutTotalResponse check;

    @Getter
    @Setter
    private CheckoutTotalResponse total;
}
