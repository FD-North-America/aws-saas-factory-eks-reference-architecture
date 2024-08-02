package com.amazonaws.saas.eks.order.dto.responses.reasoncodes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReasonCodeItemResponse {
    private String code;
    private String description;
}
