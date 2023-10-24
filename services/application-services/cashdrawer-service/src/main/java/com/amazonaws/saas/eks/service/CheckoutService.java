package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.ListCheckoutResponse;

public interface CheckoutService {
    ListCheckoutResponse get(String tenantId, String username);

    CheckoutDetailsResponse getById(String tenantId, String checkoutId);
}
