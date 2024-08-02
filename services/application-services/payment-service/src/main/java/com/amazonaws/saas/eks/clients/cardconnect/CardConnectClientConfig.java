package com.amazonaws.saas.eks.clients.cardconnect;

import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class CardConnectClientConfig {
    @Autowired
    private SecretsClient secretsClient;

    @Bean
    public RequestInterceptor authInterceptor() {
        String serviceUrl = secretsClient.getConnectServiceUrl();
        String username = secretsClient.getConnectUsername();
        String password = secretsClient.getConnectPassword();
        return new AuthRequestInterceptor(serviceUrl, username, password);
    }
}
