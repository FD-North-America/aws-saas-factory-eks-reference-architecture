package com.amazonaws.saas.eks.order.mapper;

import com.amazonaws.saas.eks.order.dto.responses.itemsinfo.ProductOrderDto;
import com.amazonaws.saas.eks.order.model.ProductOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemsInfoMapper {
    ItemsInfoMapper INSTANCE = Mappers.getMapper(ItemsInfoMapper.class);

    @Mapping(target = "orderId", expression = "java(mapId(productOrder.getId(), 1))")
    ProductOrderDto productOrderToProductOrderDto(ProductOrder productOrder);
    default String mapId(String id, int index) {
        return id.split("#")[index];
    }
}
