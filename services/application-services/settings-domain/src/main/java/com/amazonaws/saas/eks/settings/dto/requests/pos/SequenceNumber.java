package com.amazonaws.saas.eks.settings.dto.requests.pos;

import com.amazonaws.saas.eks.settings.annotation.ValueOfEnum;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SequenceNumber {
    @NotBlank
    @Size(max = 255)
    private String numberFormatSetup;

    @Min(value = 0)
    private Integer size;

    @Min(value = 0)
    private Integer nextNumber;

    @ValueOfEnum(enumClass = SequenceNumberType.class)
    private String type;
}
