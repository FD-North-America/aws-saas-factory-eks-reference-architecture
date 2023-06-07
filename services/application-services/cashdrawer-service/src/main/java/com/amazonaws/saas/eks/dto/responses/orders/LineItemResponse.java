package com.amazonaws.saas.eks.dto.responses.orders;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

public class LineItemResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private BigDecimal price;

    @Getter
    @Setter
    private BigDecimal taxPrice;

    @Getter
    @Setter
    private BigDecimal extendedPrice;

    @Getter
    @Setter
    private String taxable;

    @Getter
    @Setter
    private Integer quantity;

    @Getter
    @Setter
    private String sku;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private String type;
}
