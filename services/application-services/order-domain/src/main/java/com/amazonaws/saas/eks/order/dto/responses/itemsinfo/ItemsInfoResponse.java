package com.amazonaws.saas.eks.order.dto.responses.itemsinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemsInfoResponse {
    private List<ItemInfo> itemsInfo;
}
