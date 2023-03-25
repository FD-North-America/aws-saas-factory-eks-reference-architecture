package com.amazonaws.saas.eks.dto.responses.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.saas.eks.model.UserAttribute;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

public class UserResponse {
	private static final Logger logger = LogManager.getLogger(UserResponse.class);

	// #region User Information
	@Getter
	@Setter
	private String userId;

	@Getter
	@Setter
	private String username;

	@Getter
	@Setter
	private Boolean enabled = false;

	@Getter
	@Setter
	private String status;

	@Getter
	@Setter
	private Date created;

	@Getter
	@Setter
	private Date modified;
	// #endregion

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

	public UserResponse() { }

	public UserResponse(UserType userType) {
		// User Information
		this.username = userType.getUsername();
		this.enabled = userType.getEnabled();
		this.status = userType.getUserStatus();
		this.created = userType.getUserCreateDate();
		this.modified = userType.getUserLastModifiedDate();

		// User Attributes
		updateFromCognitoUserAttributes(userType.getAttributes());
	}

	public UserResponse(AdminGetUserResult result) {
		// User Information
		this.username = result.getUsername();
		this.enabled = result.getEnabled();
		this.status = result.getUserStatus();
		this.created = result.getUserCreateDate();
		this.modified = result.getUserLastModifiedDate();

		// User Attributes
		updateFromCognitoUserAttributes(result.getUserAttributes());
	}

	@JsonIgnore
	public List<AttributeType> getCognitoUserAttributes() {
		ArrayList<AttributeType> attributes = new ArrayList<>();

		if (!StringUtils.isEmpty(this.getFirstName())) {
			attributes.add(new AttributeType().withName(UserAttribute.FIRST_NAME.label).withValue(this.getFirstName()));
		}

		if (!StringUtils.isEmpty(this.getLastName())) {
			attributes.add(new AttributeType().withName(UserAttribute.LAST_NAME.label).withValue(this.getLastName()));
		}

		if (this.getEmail() != null) {
			attributes.add(new AttributeType().withName(UserAttribute.EMAIL.label).withValue(this.getEmail()));
		}

		attributes.add(new AttributeType().withName(UserAttribute.EMAIL_VERIFIED.label)
				.withValue(this.getEmailVerified().toString()));

		if (!StringUtils.isEmpty(this.getAddress())) {
			attributes.add(new AttributeType().withName(UserAttribute.ADDRESS.label).withValue(this.getAddress()));
		}

		if (!StringUtils.isEmpty(this.getCity())) {
			attributes.add(new AttributeType().withName(UserAttribute.CITY.label).withValue(this.getCity()));
		}

		if (!StringUtils.isEmpty(this.getState())) {
			attributes.add(new AttributeType().withName(UserAttribute.STATE.label).withValue(this.getState()));
		}

		if (!StringUtils.isEmpty(this.getCountry())) {
			attributes.add(new AttributeType().withName(UserAttribute.COUNTRY.label).withValue(this.getCountry()));
		}

		if (!StringUtils.isEmpty(this.getZip())) {
			attributes.add(new AttributeType().withName(UserAttribute.ZIP.label).withValue(this.getZip()));
		}

		if (this.getPhoneNumber() != null) {
			attributes.add(
					new AttributeType().withName(UserAttribute.PHONE_NUMBER.label).withValue(this.getPhoneNumber()));
		}

		attributes.add(
				new AttributeType().withName(UserAttribute.PHONE_NUMBER_VERIFIED.label)
						.withValue(this.getPhoneNumberVerified().toString()));

		if (!StringUtils.isEmpty(this.getHomePhoneNumber())) {
			attributes.add(new AttributeType().withName(UserAttribute.HOME_PHONE_NUMBER.label)
					.withValue(this.getHomePhoneNumber()));
		}

		if (!StringUtils.isEmpty(this.getBirthday())) {
			attributes.add(new AttributeType().withName(UserAttribute.BIRTHDAY.label).withValue(this.getBirthday()));
		}

		return attributes;
	}

	private void updateFromCognitoUserAttributes(List<AttributeType> userAttributes) {
		for (AttributeType userAttribute : userAttributes) {
			switch (UserAttribute.valueOfLabel(userAttribute.getName())) {
				case USER_ID:
					this.setUserId(userAttribute.getValue());
					break;
				case EMAIL:
					this.setEmail(userAttribute.getValue());
					break;
				case EMAIL_VERIFIED:
					this.setEmailVerified(Boolean.parseBoolean(userAttribute.getValue()));
					break;
				case FIRST_NAME:
					this.setFirstName(userAttribute.getValue());
					break;
				case LAST_NAME:
					this.setLastName(userAttribute.getValue());
					break;
				case ADDRESS:
					this.setAddress(userAttribute.getValue());
					break;
				case CITY:
					this.setCity(userAttribute.getValue());
					break;
				case STATE:
					this.setState(userAttribute.getValue());
					break;
				case COUNTRY:
					this.setCountry(userAttribute.getValue());
					break;
				case ZIP:
					this.setZip(userAttribute.getValue());
					break;
				case PHONE_NUMBER:
					this.setPhoneNumber(userAttribute.getValue());
					break;
				case PHONE_NUMBER_VERIFIED:
					this.setPhoneNumberVerified(Boolean.parseBoolean(userAttribute.getValue()));
					break;
				case HOME_PHONE_NUMBER:
					this.setHomePhoneNumber(userAttribute.getValue());
					break;
				case BIRTHDAY:
					this.setBirthday(userAttribute.getValue());
					break;
				default:
					logger.error("User attribute '" + userAttribute.getName() + "' is not supported yet.");
			}
		}
	}
}
