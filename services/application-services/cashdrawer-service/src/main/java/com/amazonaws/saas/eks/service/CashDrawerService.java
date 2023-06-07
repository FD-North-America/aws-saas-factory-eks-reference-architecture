package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.cashdrawers.CreateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.ListCashDrawersRequestParams;
import com.amazonaws.saas.eks.dto.requests.cashdrawers.UpdateCashDrawerRequest;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.CashDrawerResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.CheckoutDetailsResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.checkout.ListCashDrawerAdminResponse;
import com.amazonaws.saas.eks.dto.responses.cashdrawers.ListCashDrawersResponse;

public interface CashDrawerService {
    CashDrawerResponse create(CreateCashDrawerRequest request, String tenantId);

    CashDrawerResponse get(String cashDrawerId, String tenantId);

    CashDrawerResponse update(String cashDrawerId, UpdateCashDrawerRequest request, String tenantId);

    void delete(String cashDrawerId, String tenantId);

    ListCashDrawersResponse getAll(ListCashDrawersRequestParams params, String tenantId);

    ListCashDrawerAdminResponse getAllAdmin(ListCashDrawersRequestParams params, String tenantId);

    CheckoutDetailsResponse getCheckoutDetails(String cashDrawerId, String tenantId);
}
