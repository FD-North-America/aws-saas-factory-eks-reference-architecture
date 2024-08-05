package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.purchasing.CreatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.UpdatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;

public interface PurchasingSettingsService {
    PurchasingSettingsResponse create(String tenantId, CreatePurchasingSettingsRequest request);

    PurchasingSettingsResponse get(String tenantId);

    PurchasingSettingsResponse update(String tenantId, UpdatePurchasingSettingsRequest request);

    void delete(String tenantId);
}
