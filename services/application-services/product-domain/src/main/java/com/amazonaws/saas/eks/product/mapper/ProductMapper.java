package com.amazonaws.saas.eks.product.mapper;

import com.amazonaws.saas.eks.product.dto.requests.product.CreateProductRequest;
import com.amazonaws.saas.eks.product.dto.requests.product.UpdateProductRequest;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductResponse;
import com.amazonaws.saas.eks.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product createProductRequestToProduct(CreateProductRequest request);

    ProductResponse productToProductResponse(Product product);

    Product updateProductRequestToProduct(UpdateProductRequest request);

    ProductPricingResponse productToProductPricingResponse(Product product);

    List<ProductResponse> productsToProductResponses(List<Product> products);
}
