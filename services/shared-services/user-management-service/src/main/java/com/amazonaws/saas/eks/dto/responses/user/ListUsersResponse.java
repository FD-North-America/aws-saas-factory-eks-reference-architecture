package com.amazonaws.saas.eks.dto.responses.user;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


public class ListUsersResponse {
    @Getter
    @Setter
    private List<UserSummary> users = new ArrayList<UserSummary>();
    
    @Getter
    @Setter
    private String paginationToken;

    public int getCount() {
        return this.users.size();
    }
}
