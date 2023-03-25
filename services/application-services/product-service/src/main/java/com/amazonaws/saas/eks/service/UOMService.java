package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.dto.requests.uom.ListUOMRequestParams;
import com.amazonaws.saas.eks.dto.requests.uom.UpdateUOMRequest;
import com.amazonaws.saas.eks.dto.responses.uom.ListUOMResponse;
import com.amazonaws.saas.eks.dto.responses.uom.UOMResponse;

public interface UOMService {
    UOMResponse create(String tenantId, CreateUOMRequest request);
    UOMResponse get(String tenantId, String id);
    ListUOMResponse getAll(String tenantId, ListUOMRequestParams params);
    UOMResponse update(String tenantId, String uomId, UpdateUOMRequest request);
    void delete(String tenantId, String id);
}
