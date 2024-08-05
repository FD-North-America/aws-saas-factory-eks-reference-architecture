package com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListCheckoutResponse {
    private List<CheckoutResponse> checkouts = new ArrayList<>();
    private long count;
}
