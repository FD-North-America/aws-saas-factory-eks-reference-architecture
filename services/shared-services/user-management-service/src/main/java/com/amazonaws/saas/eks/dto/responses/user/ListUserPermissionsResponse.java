package com.amazonaws.saas.eks.dto.responses.user;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListUserPermissionsResponse {
    @Getter
    @Setter
    private Map<String, Set<String>> permissions = new HashMap<>();
}
