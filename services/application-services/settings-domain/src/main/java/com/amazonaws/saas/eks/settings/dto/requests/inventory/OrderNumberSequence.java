package com.amazonaws.saas.eks.settings.dto.requests.inventory;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OrderNumberSequence {
    @NotBlank
    @Size(max = 255)
    private String prefix;

    @NotNull
    @Min(value = 0)
    private Integer size;

    @NotBlank
    @Size(max = 255)
    private String nextNumber;
}
