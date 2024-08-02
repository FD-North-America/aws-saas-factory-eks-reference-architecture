package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.order.dto.requests.LineItemRequest;
import com.amazonaws.saas.eks.order.dto.requests.UpdateSingleLineItemRequest;
import com.amazonaws.saas.eks.order.model.ReasonCodeItem;
import com.amazonaws.saas.eks.product.dto.responses.product.ProductPricingResponse;
import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.ReasonCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {
    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    LineItem productPricingResponseToLineItem(ProductPricingResponse response);

    LineItem lineItemRequestToLineItem(LineItemRequest request);

    @Mapping(source = "lineItem.id", target = "id")
    @Mapping(source = "pricing.quantity", target = "quantity")
    @Mapping(source = "pricing.sku", target = "sku")
    @Mapping(source = "lineItem.type", target = "type")
    @Mapping(source = "lineItem.description", target = "description")
    @Mapping(source = "pricing.uomId", target = "uomId")
    @Mapping(source = "lineItem.taxable", target = "taxable")
    LineItem lineItemFromUpdateRequestAndPricing(UpdateSingleLineItemRequest lineItem, ProductPricingResponse pricing);

    ReasonCodeItem reasonCodeToReasonCodeItem(ReasonCode reasonCode);
}
