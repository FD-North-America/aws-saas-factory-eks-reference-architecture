package com.amazonaws.saas.eks.dto.requests.permission;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class CreatePermissionRequest {
    @NotBlank
    @Getter
    @Setter
    private String category;

    @NotBlank
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String group;
}
