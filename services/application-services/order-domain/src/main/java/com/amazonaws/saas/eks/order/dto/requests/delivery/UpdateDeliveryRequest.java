package com.amazonaws.saas.eks.order.dto.requests.delivery;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateDeliveryRequest {
    private String contactName;
    private String contactPhone;
    private Date requestedDeliveryDate;
    private String deliveryInstructions;
    private DeliveryLineRequest streetAddress;
    private DeliveryLineRequest city;
    private DeliveryLineRequest country;
    private DeliveryLineRequest state;
    private DeliveryLineRequest zip;
    @ValueOfEnum(enumClass = DeliveryStatus.class)
    private String status;
    private boolean outsideCityLimits;
}
