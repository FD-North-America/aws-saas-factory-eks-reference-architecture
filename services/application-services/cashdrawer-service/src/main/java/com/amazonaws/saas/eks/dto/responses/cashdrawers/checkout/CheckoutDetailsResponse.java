package com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout;

import lombok.Getter;
import lombok.Setter;

public class CheckoutDetailsResponse {
    @Getter
    @Setter
    private CheckoutTransactionDetailsResponse transactionDetails;

    @Getter
    @Setter
    private CheckoutTransactionTotalResponse transactionTotals;
}
