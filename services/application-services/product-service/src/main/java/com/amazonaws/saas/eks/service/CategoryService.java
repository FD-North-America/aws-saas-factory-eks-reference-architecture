package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.category.CreateCategoryRequest;
import com.amazonaws.saas.eks.dto.requests.category.ListCategoriesRequestParams;
import com.amazonaws.saas.eks.dto.requests.category.UpdateCategoryRequest;
import com.amazonaws.saas.eks.dto.responses.category.CategoryResponse;
import com.amazonaws.saas.eks.dto.responses.category.ListCategoriesResponse;

public interface CategoryService {
    CategoryResponse create(String tenantId, CreateCategoryRequest request);

    ListCategoriesResponse getAll(String tenantId, ListCategoriesRequestParams params);

    CategoryResponse get(String tenantId, String id);

    CategoryResponse update(String tenantId, String categoryId, UpdateCategoryRequest request);

    void delete(String tenantId, String categoryId);
}
