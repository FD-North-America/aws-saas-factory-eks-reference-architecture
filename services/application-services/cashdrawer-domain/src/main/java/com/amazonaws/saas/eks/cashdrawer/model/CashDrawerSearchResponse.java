package com.amazonaws.saas.eks.cashdrawer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CashDrawerSearchResponse {
    @Getter
    @Setter
    private List<CashDrawer> cashDrawers;

    @Getter
    @Setter
    private long count;
}
