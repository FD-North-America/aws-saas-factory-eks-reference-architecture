package com.amazonaws.saas.eks.order.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class LineItemResponse {
    private String id;

    private BigDecimal price;

    private BigDecimal extendedPrice;

    private Boolean taxable;

    private Integer quantity;

    private String sku;

    private String name;

    private Date created;

    private String type;

    private String description;

    private String uom;

    private String uomId;

    private boolean returnsAllowed;

    private List<UOMItemResponse> uomList = new ArrayList<>();

    private Integer shipped;

    private Integer backOrdered;

    private String pickupOrLoad;

    private Boolean discount;
}
