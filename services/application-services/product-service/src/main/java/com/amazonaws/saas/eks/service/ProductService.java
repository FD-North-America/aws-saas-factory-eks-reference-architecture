package com.amazonaws.saas.eks.service;


import com.amazonaws.saas.eks.product.dto.requests.product.*;
import com.amazonaws.saas.eks.product.dto.responses.product.ListProductResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.PricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;

public interface ProductService {
    ProductResponse create(String tenantId, CreateProductRequest request);

    ProductResponse get(String tenantId, String id);

    ListProductResponse getAll(String tenantId, ListProductRequestParams params);

    ProductResponse update(String tenantId, String productId, UpdateProductRequest request);

    void delete(String tenantId, String productId);

    PricingResponse getPricingDetails(String tenantId, PricingRequestParams params);

    void updateProductCounts(String tenantId, UpdateCountRequestParams params);

    /**
     * Returns a list of products that match the identifier
     * @param tenantId Tenant ID
     * @param identifier can be either Barcode, SKU, or Alternative ID
     * @return {@link ListProductResponse}
     */
    ListProductResponse getByIdentifier(String tenantId, String identifier);
}
