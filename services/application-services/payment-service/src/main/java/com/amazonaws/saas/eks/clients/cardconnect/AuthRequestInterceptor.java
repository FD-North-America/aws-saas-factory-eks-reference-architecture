package com.amazonaws.saas.eks.clients.cardconnect;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

public class AuthRequestInterceptor implements RequestInterceptor {

    private final String serviceUrl;

    private final String authValue;

    public AuthRequestInterceptor(final String serviceUrl, final String username, final String password) {
        this.serviceUrl = serviceUrl;
        this.authValue = "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
    }
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(HttpHeaders.AUTHORIZATION, authValue);
        requestTemplate.target(serviceUrl);
        Target<CardConnectClient> target = new Target.HardCodedTarget<>(CardConnectClient.class, serviceUrl);
        requestTemplate.feignTarget(target);
    }
}
