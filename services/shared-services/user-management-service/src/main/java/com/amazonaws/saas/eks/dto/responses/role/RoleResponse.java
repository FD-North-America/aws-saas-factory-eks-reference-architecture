package com.amazonaws.saas.eks.dto.responses.role;

import java.util.*;

import com.amazonaws.services.cognitoidp.model.GroupType;
import lombok.Getter;
import lombok.Setter;

public class RoleResponse {

	public RoleResponse() { }

	public RoleResponse(GroupType groupType) {
		this.name = groupType.getGroupName();
		this.description = groupType.getDescription();
		this.created = groupType.getCreationDate();
		this.modified = groupType.getLastModifiedDate();
	}

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String description;

	@Getter
	@Setter
	private Date created;

	@Getter
	@Setter
	private Date modified;

	@Getter
	@Setter
	private Map<String, Set<String>> permissionsCategories = new HashMap<>();
}
