package com.amazonaws.saas.eks.order.dto.requests;

import com.amazonaws.saas.eks.order.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.order.model.enums.PaidOutCodeType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class PaidOutCodeRequest {
    @NotBlank
    @NotNull
    @ValueOfEnum(enumClass = PaidOutCodeType.class)
    private String type;

    @NotEmpty
    private BigDecimal amount;
}
