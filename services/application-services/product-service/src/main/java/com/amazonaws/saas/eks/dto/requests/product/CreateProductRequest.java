package com.amazonaws.saas.eks.dto.requests.product;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.model.ProductInventoryStatus;
import com.amazonaws.saas.eks.model.ProductTaxable;
import com.amazonaws.saas.eks.model.ProductType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProductRequest {
    @NotBlank
    @Size(max = 80)
    @Getter
    @Setter
    private String name;

    @Size(max = 1000)
    @Getter
    @Setter
    private String description;

    @NotBlank
    @Size(max = 25)
    @Getter
    @Setter
    private String sku;

    @NotNull
    @ValueOfEnum(enumClass = ProductType.class)
    @Getter
    @Setter
    private String type;

    @NotBlank
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

    @NotNull
    @ValueOfEnum(enumClass = ProductInventoryStatus.class)
    @Getter
    @Setter
    private String inventoryStatus;

    @NotNull
    @ValueOfEnum(enumClass = ProductTaxable.class)
    @Getter
    @Setter
    private String taxable;

    @Getter
    @Setter
    private boolean returnsAllowed;

    @Getter
    @Setter
    private boolean ageVerificationRequired;

    @NotNull
    @Getter
    @Setter
    private CreateUOMRequest uom;
}
