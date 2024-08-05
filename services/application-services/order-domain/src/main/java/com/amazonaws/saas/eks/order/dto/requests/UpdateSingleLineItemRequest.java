package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UpdateSingleLineItemRequest {
    @NotBlank
    @NotNull
    private String id;

    private String uomId;

    private Integer quantity;

    private BigDecimal price;

    @NotBlank
    @NotNull
    @ValueOfEnum(enumClass = LineItemType.class)
    private String type;

    private String sku;

    private String description;

    private Date created;

    private Integer shipped;

    private Integer backOrdered;

    private String pickupOrLoad;

    private Boolean discount;

    private Boolean taxable;
}
