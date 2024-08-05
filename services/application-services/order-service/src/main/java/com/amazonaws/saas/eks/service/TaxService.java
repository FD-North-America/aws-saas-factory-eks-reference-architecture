package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.order.dto.requests.tax.CreateTaxRequest;
import com.amazonaws.saas.eks.order.dto.requests.tax.UpdateTaxRequest;
import com.amazonaws.saas.eks.order.dto.responses.tax.TaxResponse;

public interface TaxService {
    /**
     * Creates a new Tax
     * @param request {@link CreateTaxRequest}
     * @param tenantId Tenant ID
     * @return {@link TaxResponse}
     */
    TaxResponse create(CreateTaxRequest request, String tenantId);

    /**
     * Retrieves a single Tax by ID
     * @param taxId Tax ID
     * @param tenantId Tenant ID
     * @return {@link TaxResponse}
     */
    TaxResponse get(String taxId, String tenantId);

    /**
     * Retrieves a single Tax by the order ID is associated with
     * @param orderId Order ID
     * @param tenantId Tenant ID
     * @return {@link TaxResponse}
     */
    TaxResponse getByOrderId(String orderId, String tenantId);

    /**
     * Updates the tax associated with the order
     * @param taxId String
     * @param tenantId String
     * @param request {@link UpdateTaxRequest}
     * @return {@link TaxResponse}
     */
    TaxResponse update(String taxId, String tenantId, UpdateTaxRequest request);

    /**
     * Deletes a tax
     * @param taxId Tax ID
     * @param tenantId Tenant ID
     */
    void delete(String taxId, String tenantId);
}
