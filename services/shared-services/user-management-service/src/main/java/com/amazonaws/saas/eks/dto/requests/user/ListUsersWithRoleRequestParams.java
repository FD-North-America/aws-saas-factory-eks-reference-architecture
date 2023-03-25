package com.amazonaws.saas.eks.dto.requests.user;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class ListUsersWithRoleRequestParams implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int limit;

    @Getter
    @Setter
    private String nextToken;
}
