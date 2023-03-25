package com.amazonaws.saas.eks.dto.responses.user;

import lombok.Getter;
import lombok.Setter;

public class UserSummary {
	//#region User Information
	@Getter
	@Setter
	private String username;

	@Getter
	@Setter
	private Boolean enabled = false;
	//#endregion

	//#region User Attributes
	@Getter
	@Setter
	private String firstName;

	@Getter
	@Setter
	private String lastName;
	//#endregion
}
