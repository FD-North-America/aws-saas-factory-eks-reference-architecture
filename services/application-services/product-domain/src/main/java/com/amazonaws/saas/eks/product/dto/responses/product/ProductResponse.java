package com.amazonaws.saas.eks.product.dto.responses.product;

import com.amazonaws.saas.eks.product.dto.responses.uom.UOMResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String sku;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String categoryId;

    @Getter
    @Setter
    private String categoryName;

    @Getter
    @Setter
    private Integer quantityOnHand;

    @Getter
    @Setter
    private Integer minQtyOnHand;

    @Getter
    @Setter
    private Integer maxQtyOnHand;

    @Getter
    @Setter
    private BigDecimal retailPrice;

    @Getter
    @Setter
    private BigDecimal cost;

    @Getter
    @Setter
    private String inventoryStatus;

    @Getter
    @Setter
    private String taxable;

    @Getter
    @Setter
    private Boolean returnsAllowed;

    @Getter
    @Setter
    private Boolean ageVerificationRequired;

    @Getter
    @Setter
    private String vendorId;

    @Getter
    @Setter
    private String vendorName;

    @Getter
    @Setter
    private List<UOMResponse> unitsOfMeasure = new ArrayList<>();

    @Getter
    @Setter
    private String stockingUomId;

    @Getter
    @Setter
    private String quantityUomId;

    @Getter
    @Setter
    private String pricingUomId;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;
}
