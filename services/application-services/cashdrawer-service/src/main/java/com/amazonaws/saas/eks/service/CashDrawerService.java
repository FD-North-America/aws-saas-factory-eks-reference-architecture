package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.cashdrawer.dto.requests.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.cashdrawer.dto.requests.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.CashDrawerResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.ListCashDrawersResponse;
import com.amazonaws.saas.eks.cashdrawer.dto.responses.checkout.CheckoutDetailsResponse;

public interface CashDrawerService {
    CashDrawerResponse create(CreateCashDrawerRequest request, String tenantId);

    CashDrawerResponse get(String cashDrawerId, String tenantId);

    CashDrawerResponse update(String cashDrawerId, UpdateCashDrawerRequest request, String tenantId);

    void delete(String cashDrawerId, String tenantId);

    ListCashDrawersResponse getAll(ListCashDrawersRequestParams params, String tenantId);

    CheckoutDetailsResponse getCheckoutDetails(String cashDrawerId, String tenantId);

    ListCashDrawersResponse getByAssignedUser(String username, String tenantId);
}
