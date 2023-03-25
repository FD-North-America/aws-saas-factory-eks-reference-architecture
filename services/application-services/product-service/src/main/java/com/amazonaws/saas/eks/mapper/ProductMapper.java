package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.product.CreateProductRequest;
import com.amazonaws.saas.eks.dto.requests.product.UpdateProductRequest;
import com.amazonaws.saas.eks.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product createProductRequestToProduct(CreateProductRequest request);

    ProductResponse productToProductResponse(Product product);

    Product updateProductRequestToProduct(UpdateProductRequest request);
}
