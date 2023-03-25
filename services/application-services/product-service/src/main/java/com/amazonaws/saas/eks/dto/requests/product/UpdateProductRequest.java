package com.amazonaws.saas.eks.dto.requests.product;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.ProductInventoryStatus;
import com.amazonaws.saas.eks.model.ProductTaxable;
import com.amazonaws.saas.eks.model.ProductType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateProductRequest {
    @Size(max = 40)
    @Getter
    @Setter
    private String name;

    @Size(max = 1000)
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String sku;

    @ValueOfEnum(enumClass = ProductType.class)
    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String categoryId;

    @Getter
    @Setter
    private String vendorId;

    @Min(value = 0)
    @Digits(integer = 5, fraction = 0)
    @Getter
    @Setter
    private Float quantityOnHand;

    @Min(value = 0)
    @Digits(integer = 5, fraction = 0)
    @Getter
    @Setter
    private Float minQtyOnHand;

    @Min(value = 0)
    @Digits(integer = 5, fraction = 0)
    @Getter
    @Setter
    private Float maxQtyOnHand;

    @Min(value = 0)
    @Digits(integer = 7, fraction = 2)
    @Getter
    @Setter
    private BigDecimal retailPrice;

    @Min(value = 0)
    @Digits(integer = 7, fraction = 2)
    @Getter
    @Setter
    private BigDecimal cost;

    @ValueOfEnum(enumClass = ProductInventoryStatus.class)
    @Getter
    @Setter
    private String inventoryStatus;

    @ValueOfEnum(enumClass = ProductTaxable.class)
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
    private String stockingUomId;

    @Getter
    @Setter
    private String quantityUomId;

    @Getter
    @Setter
    private String pricingUomId;
}
