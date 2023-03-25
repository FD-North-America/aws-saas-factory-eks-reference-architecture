package com.amazonaws.saas.eks.dto.requests.volumepricing;

import com.amazonaws.saas.eks.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.model.VolumePricingMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class UpdateVolumePricingRequest {
    @Getter
    @Setter
    private String breakPointName;

    @Min(value = 0)
    @Getter
    @Setter
    private Integer breakPointQty;

    @ValueOfEnum(enumClass = VolumePricingMode.class)
    @Getter
    @Setter
    private String mode;

    @Min(value = 0)
    @Digits(integer = 7, fraction = 2)
    @Getter
    @Setter
    private BigDecimal discount;

    @Getter
    @Setter
    private Boolean active;
}
