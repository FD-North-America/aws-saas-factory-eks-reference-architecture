package com.amazonaws.saas.eks.order.dto.requests.delivery;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateDeliveryRequest {
    private String orderId;
    private String contactName;
    private String contactPhone;
    private Date requestedDeliveryDate;
    private String deliveryInstructions;
    private DeliveryLineRequest streetAddress;
    private DeliveryLineRequest city;
    private DeliveryLineRequest country;
    private DeliveryLineRequest state;
    private DeliveryLineRequest zip;
    private boolean outsideCityLimits;
}
