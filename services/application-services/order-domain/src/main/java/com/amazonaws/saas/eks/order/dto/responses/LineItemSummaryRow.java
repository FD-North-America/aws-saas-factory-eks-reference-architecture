package com.amazonaws.saas.eks.order.dto.responses;

import com.amazonaws.saas.eks.order.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LineItemSummaryRow {
    private String sku;
    private String name;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal price;
    private int quantity;
}
