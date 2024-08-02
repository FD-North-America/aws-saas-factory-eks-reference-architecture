package com.amazonaws.saas.eks.order.mapper;

import com.amazonaws.saas.eks.order.dto.requests.delivery.CreateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.requests.delivery.UpdateDeliveryRequest;
import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryResponse;
import com.amazonaws.saas.eks.order.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeliveryMapper {
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    Delivery createDeliveryRequestToDelivery(CreateDeliveryRequest request);

    DeliveryResponse deliveryToDeliveryResponse(Delivery delivery);

    Delivery updateDeliveryRequestToDelivery(UpdateDeliveryRequest request);
}
