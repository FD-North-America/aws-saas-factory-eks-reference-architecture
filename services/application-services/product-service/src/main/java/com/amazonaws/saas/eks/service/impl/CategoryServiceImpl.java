package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.requests.category.CreateCategoryRequest;
import com.amazonaws.saas.eks.dto.requests.category.ListCategoriesRequestParams;
import com.amazonaws.saas.eks.dto.requests.category.UpdateCategoryRequest;
import com.amazonaws.saas.eks.dto.responses.category.CategoryResponse;
import com.amazonaws.saas.eks.dto.responses.category.ListCategoriesResponse;
import com.amazonaws.saas.eks.error.DependencyViolationErrorItem;
import com.amazonaws.saas.eks.exception.ProductCategoryInvalidDeleteException;
import com.amazonaws.saas.eks.mapper.CategoryMapper;
import com.amazonaws.saas.eks.model.Category;
import com.amazonaws.saas.eks.model.Product;
import com.amazonaws.saas.eks.repository.CategoryRepository;
import com.amazonaws.saas.eks.repository.ProductRepository;
import com.amazonaws.saas.eks.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CategoryResponse create(String tenantId, CreateCategoryRequest request) {
        Category category = CategoryMapper.INSTANCE.createCategoryRequestToCategory(request);
        category.setCreated(new Date());
        category.setModified(category.getCreated());

        Category createdCategory = categoryRepository.insert(tenantId, request.getParentId(), category);
        return CategoryMapper.INSTANCE.categoryToCategoryResponse(createdCategory);
    }

    @Override
    public ListCategoriesResponse getAll(String tenantId, ListCategoriesRequestParams params) {
        ListCategoriesResponse response = new ListCategoriesResponse();
        List<Category> categories = categoryRepository.getAll(tenantId, params.getFilter(), params.getLevel());
        for (Category c: categories) {
            CategoryResponse cr = StringUtils.isEmpty(params.getLevel())
                    ? CategoryMapper.INSTANCE.categoryToCategoryResponse(c)
                    : CategoryMapper.INSTANCE.categoryToSimplerCategoryResponse(c);
           response.getCategories().add(cr);
        }
        return response;
    }

    @Override
    public CategoryResponse get(String tenantId, String id) {
        Category category = categoryRepository.get(tenantId, id);
        return CategoryMapper.INSTANCE.categoryToCategoryResponse(category);
    }

    @Override
    public CategoryResponse update(String tenantId, String id, UpdateCategoryRequest request) {
        Category category = CategoryMapper.INSTANCE.updateCategoryRequestToCategory(request);
        Category updatedCategory = categoryRepository.update(tenantId, id, request.getNewParentId(), category);
        updateCategoryProducts(tenantId, updatedCategory);
        return CategoryMapper.INSTANCE.categoryToCategoryResponse(updatedCategory);
    }

    @Override
    public void delete(String tenantId, String categoryId) {
        Category model = categoryRepository.get(tenantId, categoryId);
        if (model.getCategories().size() > 0) {
            List<DependencyViolationErrorItem> items = model.getCategories().stream()
                    .map(m -> new DependencyViolationErrorItem(m.getId(), m.getName()))
                    .collect(Collectors.toList());
            throw new ProductCategoryInvalidDeleteException(categoryId, model.getLevel(), CategoryRepository.STORE_ID,
                    Category.class.getSimpleName(), items);
        }

        List<Product> products = productRepository.getCategoryProducts(tenantId, categoryId);
        if (!products.isEmpty()) {
            List<DependencyViolationErrorItem> items = products.stream()
                    .map(p -> new DependencyViolationErrorItem(p.getId(), p.getName()))
                    .collect(Collectors.toList());
            throw new ProductCategoryInvalidDeleteException(categoryId, model.getLevel(), CategoryRepository.STORE_ID,
                    Product.class.getSimpleName(), items);
        }

        categoryRepository.delete(model);
    }

    private void updateCategoryProducts(String tenantId, Category category) {
        List<Product> categoryProducts = productRepository.getCategoryProducts(tenantId, category.getId());
        for (Product p : categoryProducts) {
            p.setCategoryName(category.getName());
            p.setCategoryPath(category.getCategoryPath());
        }
        productRepository.batchUpdate(categoryProducts);
    }
}
