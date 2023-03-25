package com.amazonaws.saas.eks.dto.requests.permission;

import lombok.Getter;
import lombok.Setter;


public class UpdatePermissionRequest {
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
