package com.amazonaws.saas.eks.settings.model.v2.inventory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderNumberSequence {
    private String prefix;
    private Integer size;
    private String nextNumber;
}
