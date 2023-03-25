package com.amazonaws.saas.eks.dto.responses.role;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class RoleSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String description;

	public RoleSummary() {
	}

	public RoleSummary(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
