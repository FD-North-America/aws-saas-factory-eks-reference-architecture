package com.amazonaws.saas.eks.dto.requests.uom;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class UpdateUOMRequest {
    @Size(max = 40)
    @Getter
    @Setter
    private String name;

    @Min(value = 0)
    @Digits(integer = 5, fraction = 4)
    @Getter
    @Setter
    private Double factor;

    @Size(max = 12)
    @Getter
    @Setter
    private String barcode;

    @Getter
    @Setter
    private String alternateId;
}
