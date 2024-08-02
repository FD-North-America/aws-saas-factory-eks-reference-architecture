package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.product.dto.requests.category.CreateCategoryRequest;
import com.amazonaws.saas.eks.product.dto.requests.category.ListCategoriesRequestParams;
import com.amazonaws.saas.eks.product.dto.requests.category.UpdateCategoryRequest;
import com.amazonaws.saas.eks.product.dto.responses.category.CategoryResponse;
import com.amazonaws.saas.eks.product.dto.responses.category.ListCategoriesResponse;
import com.amazonaws.saas.eks.product.model.Permission;
import com.amazonaws.saas.eks.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CategoryController {
    private static final Logger logger = LogManager.getLogger(CategoryController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('" + Permission.CATEGORY_CREATE + "')")
    @PostMapping(value = "{tenantId}/products/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public CategoryResponse create(@RequestBody @Valid CreateCategoryRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return categoryService.create(tu.getTenantId(), request);
        } catch (Exception e) {
            logger.error("Error creating category", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CATEGORY_READ + "')")
    @GetMapping(value = "{tenantId}/products/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListCategoriesResponse getAll(ListCategoriesRequestParams params, HttpServletRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return categoryService.getAll(tu.getTenantId(), params);
        } catch (Exception e) {
            logger.error("Error listing categories", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CATEGORY_READ + "')")
    @GetMapping(value = "{tenantId}/products/categories/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public CategoryResponse get(@PathVariable("categoryId") String categoryId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return categoryService.get(tu.getTenantId(), categoryId);
        } catch (Exception e) {
            logger.error(String.format("Category not found with ID: %s", categoryId), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CATEGORY_UPDATE + "')")
    @PutMapping(value = "{tenantId}/products/categories/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public CategoryResponse update(@PathVariable("categoryId") String categoryId,
                                   @RequestBody @Valid UpdateCategoryRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            return categoryService.update(tu.getTenantId(), categoryId, request);
        } catch (Exception e) {
            logger.error("Error updating category", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('" + Permission.CATEGORY_DELETE + "')")
    @DeleteMapping(value = "{tenantId}/products/categories/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void delete(@PathVariable("categoryId") String categoryId) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            categoryService.delete(tu.getTenantId(), categoryId);
        } catch (Exception e) {
            logger.error("Error deleting category", e);
            throw e;
        }
    }
}
