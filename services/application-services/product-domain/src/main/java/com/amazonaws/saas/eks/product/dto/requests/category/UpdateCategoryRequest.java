package com.amazonaws.saas.eks.product.dto.requests.category;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateCategoryRequest {
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String name;

    @Size(max = 20)
    @Getter
    @Setter
    private String description;

    @Size(max = 10)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String newParentId;
}
