package com.amazonaws.saas.eks.service;


import com.amazonaws.saas.eks.product.dto.requests.volumepricing.CreateVolumePricingRequest;
import com.amazonaws.saas.eks.product.dto.requests.volumepricing.ListVolumePricingRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.volumepricing.UpdateVolumePricingRequest;
import com.amazonaws.saas.eks.product.dto.responses.volumepricing.ListVolumePricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.volumepricing.VolumePricingResponse;

public interface VolumePricingService {
    VolumePricingResponse create(String tenantId, CreateVolumePricingRequest request);

    VolumePricingResponse get(String tenantId, String id);

    ListVolumePricingResponse getAll(String tenantId, ListVolumePricingRequestParams params);

    VolumePricingResponse update(String tenantId, String id, UpdateVolumePricingRequest request);

    void delete(String tenantId, String id);
}
