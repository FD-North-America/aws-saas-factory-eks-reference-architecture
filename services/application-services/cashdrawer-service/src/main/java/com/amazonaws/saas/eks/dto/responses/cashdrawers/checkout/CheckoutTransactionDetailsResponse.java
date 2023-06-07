package com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout;

import com.amazonaws.saas.eks.dto.responses.TransactionResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CheckoutTransactionDetailsResponse {
    @Getter
    @Setter
    private List<TransactionResponse> transactions = new ArrayList<>();

    @Getter
    @Setter
    private long count;
}
