package com.amazonaws.saas.eks.dto.responses.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class PermissionResponse {
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
    private String category;

    @Getter
    @Setter
    private String group;

    @Getter
    @Setter
    private Date created;

    @Getter
    @Setter
    private Date modified;
}
