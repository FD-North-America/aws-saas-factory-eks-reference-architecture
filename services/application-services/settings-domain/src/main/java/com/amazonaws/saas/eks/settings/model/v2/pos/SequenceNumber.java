package com.amazonaws.saas.eks.settings.model.v2.pos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SequenceNumber {
    private String numberFormatSetup;
    private Integer size;
    private Integer nextNumber;
    private String type;
}
