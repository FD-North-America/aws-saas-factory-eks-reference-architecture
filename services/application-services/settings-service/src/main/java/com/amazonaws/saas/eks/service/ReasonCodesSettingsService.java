package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.CreateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.UpdateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;

public interface ReasonCodesSettingsService {
    ReasonCodesSettingsResponse create(String tenantId, CreateReasonCodesSettingsRequest request);

    ReasonCodesSettingsResponse get(String tenantId);

    ReasonCodesSettingsResponse update(String tenantId, UpdateReasonCodesSettingsRequest request);

    void delete(String tenantId, String code);
}
