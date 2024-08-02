package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.settings.dto.requests.salestax.CreateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.ListSalesTaxSettingsRequestParams;
import com.amazonaws.saas.eks.settings.dto.requests.salestax.UpdateSalesTaxSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ListSalesTaxSettingsResponse;
import com.amazonaws.saas.eks.settings.dto.responses.SalesTaxSettingsResponse;

public interface SalesTaxSettingsService {
    SalesTaxSettingsResponse create(String tenantId, CreateSalesTaxSettingsRequest request);

    SalesTaxSettingsResponse get(String tenantId, String id);

    ListSalesTaxSettingsResponse getAll(String tenantId, ListSalesTaxSettingsRequestParams params);

    SalesTaxSettingsResponse update(String tenantId, String id, UpdateSalesTaxSettingsRequest request);

    void delete(String tenantId, String salesTaxSettingsId);
}
