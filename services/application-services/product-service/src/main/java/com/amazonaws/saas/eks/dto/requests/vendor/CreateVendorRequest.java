package com.amazonaws.saas.eks.dto.requests.vendor;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateVendorRequest {
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String name;

    @Size(max = 40)
    @Getter
    @Setter
    private String description;
}
