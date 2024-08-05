package com.amazonaws.saas.eks.clients;

import com.amazonaws.saas.eks.auth.client.AuthInterceptor;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ClientConfig {
    @Bean
    public RequestInterceptor authInterceptor(
            @Value("${auth.url}") final String authUrl,
            @Value("${auth.clientId}") final String clientId,
            @Value("${auth.clientSecret}") final String clientSecret) {
        return new AuthInterceptor(authUrl, clientId, clientSecret);
    }
}
