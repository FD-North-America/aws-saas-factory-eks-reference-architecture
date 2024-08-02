package com.amazonaws.saas.eks.order.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChargeCodeListResponse {
    private String orderId;
    private List<ChargeCodeListItem> chargeCodes;
    private int count;
}
