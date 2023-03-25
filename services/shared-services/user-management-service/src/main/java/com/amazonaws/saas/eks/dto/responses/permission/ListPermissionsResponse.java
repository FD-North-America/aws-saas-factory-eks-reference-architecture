package com.amazonaws.saas.eks.dto.responses.permission;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ListPermissionsResponse {
    @Getter
    @Setter
    private List<PermissionResponse> permissions = new ArrayList<>();

    public int getCount() {
        return this.permissions.size();
    }
}
