package com.amazonaws.saas.eks.settings.dto.requests.inventory;

import com.amazonaws.saas.eks.settings.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.settings.model.enums.StoreLocationCodeType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class StoreLocationCode {
    @NotBlank
    @Size(max = 255)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String description;

    @ValueOfEnum(enumClass = StoreLocationCodeType.class)
    private String type;
}
