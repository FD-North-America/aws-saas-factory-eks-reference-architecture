package com.amazonaws.saas.eks.dto.requests.orders;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.enums.LineItemType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class LineItemRequest {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String uomId;

    @Getter
    @Setter
    private Integer quantity;

    @Getter
    @Setter
    private String barcode;

    @Getter
    @Setter
    private BigDecimal price;

    @NotBlank
    @NotNull
    @ValueOfEnum(enumClass = LineItemType.class)
    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private Date created;
}
