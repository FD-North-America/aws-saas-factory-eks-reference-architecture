package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.inventory.CreateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.UpdateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.InventorySettingsResponse;

public interface InventorySettingsService {
    InventorySettingsResponse create(String tenantId, CreateInventorySettingsRequest request);

    InventorySettingsResponse get(String tenantId);

    InventorySettingsResponse update(String tenantId, UpdateInventorySettingsRequest request);

    void delete(String tenantId);
}