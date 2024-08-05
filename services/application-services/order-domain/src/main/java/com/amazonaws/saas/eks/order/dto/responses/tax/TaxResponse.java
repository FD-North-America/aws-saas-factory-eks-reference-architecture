package com.amazonaws.saas.eks.order.dto.responses.tax;

import com.amazonaws.saas.eks.order.dto.responses.delivery.DeliveryLineResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaxResponse {
    private String id;

    private String orderId;

    private String type;

    private String exemptCode;

    private String certificateId;

    private String streetAddress;

    private DeliveryLineResponse city;

    private DeliveryLineResponse county;

    private DeliveryLineResponse state;

    private String zip;

    private Date created;

    private Date modified;
}
