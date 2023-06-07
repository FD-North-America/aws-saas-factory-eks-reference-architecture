package com.amazonaws.saas.eks.model;

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
