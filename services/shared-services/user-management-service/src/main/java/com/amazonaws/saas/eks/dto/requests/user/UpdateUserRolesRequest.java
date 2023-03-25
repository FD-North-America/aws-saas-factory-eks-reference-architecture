package com.amazonaws.saas.eks.dto.requests.user;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


public class UpdateUserRolesRequest implements Serializable {
	private static final long serialVersionUID = 1L;
    
    @NotNull
	@Getter
	@Setter
	private List<String> roles;
}
