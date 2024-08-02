package com.amazonaws.saas.eks.clients.cardsecure;

import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import feign.RequestInterceptor;
import feign.Target;
import org.springframework.context.annotation.Bean;

public class CardSecureClientConfig {

    private final String serviceUrl;

    public CardSecureClientConfig(SecretsClient secretsClient) {
        this.serviceUrl = secretsClient.getCardSecureServiceUrl();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.target(serviceUrl);
            Target<CardSecureClient> target = new Target.HardCodedTarget<>(
                    CardSecureClient.class, serviceUrl);
            requestTemplate.feignTarget(target);
        };
    }
}
