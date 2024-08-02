package com.amazonaws.saas.eks.dto.requests;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.enums.ProductInventoryStatus;
import com.amazonaws.saas.eks.model.enums.StockLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class StockStatusReportRequest {
    @Getter
    @Setter
    private List<String> categoryIds = new ArrayList<>();

    @ValueOfEnum(enumClass = StockLevel.class)
    @Getter
    @Setter
    private String stockLevel;

    @ValueOfEnum(enumClass = ProductInventoryStatus.class)
    @Getter
    @Setter
    private String itemStatus;

    @Getter
    @Setter
    private String vendor;

    @Getter
    @Setter
    private Integer from;

    @Getter
    @Setter
    private Integer size;
}
