package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.pos.CreatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.pos.UpdatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;

public interface POSSettingsService {
    POSSettingsResponse create(String tenantId, CreatePOSSettingsRequest request);

    POSSettingsResponse get(String tenantId);

    POSSettingsResponse update(String tenantId, UpdatePOSSettingsRequest request);

    void delete(String tenantId);

    String getNextSequence(String tenantId, SequenceNumberType type);
}
