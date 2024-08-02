package com.amazonaws.saas.eks.cashdrawer.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ListCashDrawersResponse {
    @Getter
    @Setter
    private List<CashDrawerResponse> cashDrawers = new ArrayList<>();

    @Getter
    @Setter
    private long count;
}
