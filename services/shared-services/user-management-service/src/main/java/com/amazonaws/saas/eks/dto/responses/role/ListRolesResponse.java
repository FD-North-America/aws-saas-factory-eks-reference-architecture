package com.amazonaws.saas.eks.dto.responses.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ListRolesResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private List<RoleSummary> roles = new ArrayList<>();

    public int getCount() {
        return this.roles.size();
    }

    @Getter
    @Setter
    private String nextToken;
}
