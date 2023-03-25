package com.amazonaws.saas.eks.dto.requests.volumepricing;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class ListVolumePricingRequestParams {
    @NotEmpty
    @Getter
    @Setter
    private String productId;
}
