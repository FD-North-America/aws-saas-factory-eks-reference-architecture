package com.amazonaws.saas.eks.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem implements Serializable {
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

    private BigDecimal cost;

    private boolean returnsAllowed;

    private List<UOMItem> uomList = new ArrayList<>();

    private Integer shipped;

    private Integer backOrdered;

    private String pickupOrLoad;

    private Boolean discount;

    private String categoryId;

    private BigDecimal taxAmount;

    @Override
    public String toString() {
        return "LineItem [id=" + id + ", price=" + price + ", quantity=" + quantity + "]";
    }
}
