package com.amazonaws.saas.eks.order.dto.responses.delivery;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DeliveryResponse {
    private String id;
    private String orderId;
    private String contactName;
    private String contactPhone;
    private Date requestedDeliveryDate;
    private String deliveryInstructions;
    private DeliveryLineResponse streetAddress;
    private DeliveryLineResponse city;
    private DeliveryLineResponse country;
    private DeliveryLineResponse state;
    private DeliveryLineResponse zip;
    private String status;
    private boolean outsideCityLimits;
}
