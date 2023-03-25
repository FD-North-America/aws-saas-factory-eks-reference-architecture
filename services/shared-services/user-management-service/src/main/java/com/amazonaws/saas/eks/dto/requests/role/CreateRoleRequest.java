
package com.amazonaws.saas.eks.dto.requests.role;

import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class CreateRoleRequest {
	@NotBlank
	@Size(min = 3, max = 128)
	@Pattern(regexp = "^[a-zA-Z0-9 ]+$")
	@Getter
	@Setter
	private String name;

	@Size(max = 2048)
	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private Map<String, Set<String>> permissionsCategories;

	public void transformName() {
		this.name = this.name.trim().replaceAll("\\s+", "-");
	}
}
