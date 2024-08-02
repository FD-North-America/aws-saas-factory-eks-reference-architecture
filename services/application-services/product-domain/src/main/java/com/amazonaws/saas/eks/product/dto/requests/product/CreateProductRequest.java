package com.amazonaws.saas.eks.product.dto.requests.product;

import com.amazonaws.saas.eks.product.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.product.dto.requests.uom.CreateUOMRequest;
import com.amazonaws.saas.eks.product.model.enums.ProductInventoryStatus;
import com.amazonaws.saas.eks.product.model.enums.ProductTaxable;
import com.amazonaws.saas.eks.product.model.enums.ProductType;
import com.amazonaws.util.StringUtils;
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
    private boolean returnsAllowed;

    @Getter
    @Setter
    private boolean ageVerificationRequired;

    @NotNull
    @Getter
    @Setter
    private CreateUOMRequest uom;

    @AssertTrue(message = "The Generic Product requires the values of Name, SKU, Type, CategoryId, UOM Name and UOM Factor")
    private boolean isValidGenericProduct() {
        if (type == null || !type.equals(ProductType.GENERIC.toString())) {
            return true;
        }
        return hasValuesMinRequiredFields();
    }

    @AssertTrue(message = "The Normal Product requires at least the values of Name, SKU, Type, CategoryId, UOM Name, " +
            "UOM Factor, Inventory Status and Taxable")
    private boolean isValidNormalProduct() {
        if (type == null || !type.equals(ProductType.NORMAL.toString())) {
            return true;
        }
        return hasValuesMinRequiredFields() && inventoryStatus != null && taxable != null;
    }

    private boolean hasValuesMinRequiredFields() {
        return (StringUtils.hasValue(name) && StringUtils.hasValue(sku) && StringUtils.hasValue(categoryId)
                && uom != null && StringUtils.hasValue(uom.getName()) && uom.getFactor() != null);
    }
}
