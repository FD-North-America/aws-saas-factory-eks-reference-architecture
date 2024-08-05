package com.amazonaws.saas.eks.dto.responses.cashdrawercheckoutreport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashDrawerCheckoutReportResponse {
    private CashDrawerDetail cashDrawer;

    private TransactionRecap transactionRecap;

    private CashDrawerTransactions cashDrawerTransactions;

    private CashOutCount cashOutCount;
}
