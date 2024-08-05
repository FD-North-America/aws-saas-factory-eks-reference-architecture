package com.amazonaws.saas.eks.settings.dto.requests.salestax;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateSalesTaxSettingsRequest {
    @Size(max = 20)
    private String code;

    @Size(max = 60)
    private String description;

    @Digits(integer = 3, fraction = 2)
    private Float rate;

    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal taxableLimit;

    private String taxingState;

    private String newParentId;
}
