
package com.amazonaws.saas.eks.dto.requests.role;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

public class UpdateRoleRequest {

	@Size(max = 2048)
	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private Map<String, Set<String>> permissionsCategories;
}
