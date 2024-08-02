package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.ListCheckoutResponse;
import com.amazonaws.saas.eks.cashdrawer.model.CashDrawer;
import com.amazonaws.saas.eks.cashdrawer.model.enums.CashDrawerStatus;

public interface CheckoutService {
    /**
     * Get all checkouts for a user
     * @param tenantId tenant id
     * @param username username
     * @return {@link ListCheckoutResponse}
     */
    ListCheckoutResponse get(String tenantId, String username);

    /**
     * Get checkout details by checkout id
     * @param tenantId tenant id
     * @param checkoutId checkout id
     * @return {@link CheckoutDetailsResponse}
     */
    CheckoutDetailsResponse getById(String tenantId, String checkoutId);

    /**
     * Process the checkout request
     * @param tenantId tenant id
     * @param status status
     * @param oldCashDrawer {@link CashDrawer}
     * @param updatedCashDrawer {@link CashDrawer}
     * @return {@link CashDrawer}
     */
    CashDrawer process(String tenantId,
                       String status,
                       CashDrawer oldCashDrawer,
                       CashDrawer updatedCashDrawer);
}
