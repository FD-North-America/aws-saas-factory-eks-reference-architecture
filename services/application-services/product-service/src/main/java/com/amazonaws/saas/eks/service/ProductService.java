package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.product.*;
import com.amazonaws.saas.eks.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.dto.responses.product.ProductResponse;

public interface ProductService {
    ProductResponse create(String tenantId, CreateProductRequest request);

    ProductResponse get(String tenantId, String id);

    ListProductResponse getAll(String tenantId, ListProductRequestParams params);

    ProductResponse update(String tenantId, String productId, UpdateProductRequest request);

    void delete(String tenantId, String productId);
}
