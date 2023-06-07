package com.amazonaws.saas.eks.clients.product.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

public class VolumePricingResponse {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String breakPointName;

    @Getter
    @Setter
    private Integer breakPointQty;

    @Getter
    @Setter
    private String uomId;

    @Getter
    @Setter
    private String productId;

    @Getter
    @Setter
    private String mode;

    @Getter
    @Setter
    private Double discount;

    @Getter
    @Setter
    private Boolean active;

    @Getter
    @Setter
    private Double factor;

    @Getter
    @Setter
    private BigDecimal price;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;
}
