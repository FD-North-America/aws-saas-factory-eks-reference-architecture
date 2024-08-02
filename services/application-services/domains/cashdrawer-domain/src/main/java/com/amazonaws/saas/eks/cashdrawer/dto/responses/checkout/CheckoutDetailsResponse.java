package com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerTrayResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CheckoutDetailsResponse {
    private CheckoutTransactionDetailsResponse transactionDetails;
    private CheckoutTransactionTotalResponse transactionTotals;
    private List<CashDrawerTrayResponse> trays;
    private BigDecimal traysTotalAmount;
    private BigDecimal startUpAmount;
    private BigDecimal cashTotalAmount;
    private BigDecimal cardTotalAmount;
}
