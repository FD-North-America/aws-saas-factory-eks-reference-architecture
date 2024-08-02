package com.amazonaws.saas.eks.order.dto.requests.itemsinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemsInfoRequest {
    private String[] productIds;
}
