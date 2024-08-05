package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.order.dto.requests.CreateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateChargeCodeRequest;
import com.amazonaws.saas.eks.order.dto.responses.ChargeCodeResponse;

public interface ChargeCodeService {
    /**
     * Adds a charge code to the specified order
     * @param tenantId String
     * @param request {@link CreateChargeCodeRequest}
     * @return {@link ChargeCodeResponse}
     */
    ChargeCodeResponse create(String tenantId, CreateChargeCodeRequest request);

    /**
     * Returns the charge code associated with the order
     * @param tenantId String
     * @param chargeCodeId String
     * @return {@link ChargeCodeResponse}
     */
    ChargeCodeResponse get(String tenantId, String chargeCodeId);

    /**
     * Updates the charge code associated with the order
     * @param tenantId String
     * @param chargeCodeId String
     * @param request {@link UpdateChargeCodeRequest}
     * @return {@link ChargeCodeResponse}
     */
    ChargeCodeResponse update(String tenantId, String chargeCodeId, UpdateChargeCodeRequest request);

    /**
     * Deletes a charge code associated with the order
     * @param tenantId String
     * @param chargeCodeId String
     */
    void delete(String tenantId, String chargeCodeId);
}
