package com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListCashDrawerAdminResponse {
    @Getter
    @Setter
    private List<CashDrawerAdminResponse> cashDrawers = new ArrayList<>();

    @Getter
    @Setter
    private long count;
}
