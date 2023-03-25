package com.amazonaws.saas.eks.dto.requests.user;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class UpdateUserRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	// #region User Attributes
	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private Boolean emailVerified = false;

	@Getter
	@Setter
	private String firstName;

	@Getter
	@Setter
	private String lastName;

	@Getter
	@Setter
	private String address;

	@Getter
	@Setter
	private String city;

	@Getter
	@Setter
	private String state;

	@Getter
	@Setter
	private String country;

	@Getter
	@Setter
	private String zip;

	@Getter
	@Setter
	private String phoneNumber;

	@Getter
	@Setter
	private Boolean phoneNumberVerified = false;

	@Getter
	@Setter
	private String homePhoneNumber;

	@Getter
	@Setter
	private String birthday;
	// #endregion
}
