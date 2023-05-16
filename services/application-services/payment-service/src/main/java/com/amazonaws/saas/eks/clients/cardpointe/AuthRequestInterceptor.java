package com.amazonaws.saas.eks.clients.cardpointe;

import com.amazonaws.saas.eks.handlerinterceptor.SessionHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class AuthRequestInterceptor implements RequestInterceptor {
    @Autowired
    private SessionHolder sessionHolder;

    private final String authorization;
    private final String serviceUrl;

    public AuthRequestInterceptor(final String authorization, final String serviceUrl) {
        this.authorization = authorization;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(HttpHeaders.AUTHORIZATION, authorization);
        if (sessionHolder.getSessionKey() != null) {
            requestTemplate.header("X-CardConnect-SessionKey", sessionHolder.getSessionKey());
        }
        requestTemplate.target(serviceUrl);
        Target<CardPointeServiceClient> target = new Target.HardCodedTarget<>(
                CardPointeServiceClient.class, serviceUrl);
        requestTemplate.feignTarget(target);
    }
}
