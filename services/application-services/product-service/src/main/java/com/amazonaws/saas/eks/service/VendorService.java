package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.vendor.CreateVendorRequest;
import com.amazonaws.saas.eks.dto.requests.vendor.ListVendorsRequestParams;
import com.amazonaws.saas.eks.dto.requests.vendor.UpdateVendorRequest;
import com.amazonaws.saas.eks.dto.responses.vendor.ListVendorResponse;
import com.amazonaws.saas.eks.dto.responses.vendor.VendorResponse;

public interface VendorService {
    VendorResponse create(String tenantId, CreateVendorRequest request);

    VendorResponse get(String tenantId, String id);

    VendorResponse update(String tenantId, String id, UpdateVendorRequest request);

    void delete(String tenantId, String id);

    ListVendorResponse getAll(String tenantId, ListVendorsRequestParams params);
}
