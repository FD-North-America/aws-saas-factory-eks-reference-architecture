package com.amazonaws.saas.eks.cashdrawer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CashDrawerCheckoutSearchResponse {
    private List<CashDrawerCheckout> checkouts;
    private long count;
}
