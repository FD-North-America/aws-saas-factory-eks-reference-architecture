package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.category.CreateCategoryRequest;
import com.amazonaws.saas.eks.dto.requests.category.UpdateCategoryRequest;
import com.amazonaws.saas.eks.dto.responses.category.CategoryResponse;
import com.amazonaws.saas.eks.model.Category;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category createCategoryRequestToCategory(CreateCategoryRequest request);

    @Named("categoryToCategoryResponse")
    CategoryResponse categoryToCategoryResponse(Category category);

    @Named("categoryToSimplerCategoryResponse")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target="id", source="id")
    @Mapping(target="name", source="name")
    @Mapping(target="code", source="code")
    CategoryResponse categoryToSimplerCategoryResponse(Category category);

    Category updateCategoryRequestToCategory(UpdateCategoryRequest request);
}
