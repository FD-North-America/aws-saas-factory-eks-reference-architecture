package com.amazonaws.saas.eks.clients.cardpointe;

import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class CardPointeServiceClientConfig {
    @Autowired
    private SecretsClient secretsClient;

    @Bean
    public RequestInterceptor authInterceptor() {
        String authorization = secretsClient.getPaymentServiceAuth();
        String serviceUrl = secretsClient.getPaymentServiceUrl();
        return new AuthRequestInterceptor(authorization, serviceUrl);
    }
}
