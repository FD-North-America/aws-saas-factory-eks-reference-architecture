package com.amazonaws.saas.eks.dto.requests.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class CreateUserRequest {
	// #region User Information
	@NotBlank
	@Size(min = 3, max = 128)
	@Pattern(regexp = "[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]+")
	@Getter
	@Setter
	private String username;
	// #endregion

	@NotBlank
	@Getter
	@Setter
	private String temporaryPassword;

	// #region User Attributes
	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private Boolean emailVerified = false;

	@NotBlank
	@Getter
	@Setter
	private String firstName;

	@NotBlank
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
