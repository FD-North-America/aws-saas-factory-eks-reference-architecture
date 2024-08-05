package com.amazonaws.saas.eks.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretsConfig {
    @Bean
    public AWSSecretsManager awsSecretsManagerClient(@Value("${aws.region}") final String region) {
        return AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();
    }
}
