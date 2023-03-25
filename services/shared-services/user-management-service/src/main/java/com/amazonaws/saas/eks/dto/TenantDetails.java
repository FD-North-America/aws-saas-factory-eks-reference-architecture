package com.amazonaws.saas.eks.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class TenantDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter @Setter private String tenantId;

	@Getter @Setter private String customDomain;
	
	@Getter @Setter private String hostedZoneId;
	
	@Getter @Setter private String appCloudFrontId;
	
	@Getter @Setter private String userPoolId;
	
	@Getter @Setter private String appCloudFrontDomainName;
	
	@Getter @Setter private String authServer;
	
	@Getter @Setter private String clientId;
	
	@Getter @Setter private String redirectUrl;
	
	@Getter @Setter private String silentRefreshRedirectUri;
	
	@Getter @Setter private String email;
	
	@Getter @Setter private String password;
	
	@Getter @Setter private String companyName;
	
	@Getter @Setter private String region;
	
	@Getter @Setter private String cognitoDomain;
}
