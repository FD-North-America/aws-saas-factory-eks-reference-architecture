package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.DiscountType;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class LineItemRequest {
    private String id;

    private String uomId;

    private Integer quantity;

    private String barcode;

    private BigDecimal price;

    @NotBlank
    @NotNull
    @ValueOfEnum(enumClass = LineItemType.class)
    private String type;

    private String sku;

    private String description;

    private String name;

    private Date created;

    @ValueOfEnum(enumClass = DiscountType.class)
    private String discountType;

    private String discountReason;
}
