package com.amazonaws.saas.eks.secretsmanager;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretsClient {
    private static final Logger logger = LogManager.getLogger(SecretsClient.class);

    @Getter
    private final String secretName;

    @Autowired
    private AWSSecretsManager secretsManager;

    public SecretsClient(@Value("${aws.secret.name}") final String secretName) {
        this.secretName = secretName;
    }

    public SecretsHolder getSecrets() {
        String secretValue = null;
        try {
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId(secretName);
            GetSecretValueResult getSecretValueResult = secretsManager.getSecretValue(getSecretValueRequest);
            secretValue = getSecretValueResult.getSecretString();
        } catch (ResourceNotFoundException e) {
            logger.error(String.format("The requested secret %s was not found", secretName));
        } catch (InvalidRequestException e) {
            logger.error(String.format("The request was invalid: %s", e));
        } catch (InvalidParameterException e) {
            logger.error(String.format("The request had invalid params: %s", e));
        } catch (Exception e) {
            logger.error(String.format("An error occurred while retrieving the secret %s: %s", secretName, e));
        }

        if (secretValue != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(secretValue, SecretsHolder.class);
            } catch (JsonProcessingException e) {
                logger.error(String.format("An error occurred while mapping the secret value %s: %s", secretValue, e));
            }
        }

        return null;
    }

    public String getEncryptionKey() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getEncryptionKey();
        }
        return "";
    }

    public String getPaymentServiceUrl() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardPointeServiceUrl();
        }
        return "";
    }

    public String getPaymentServiceAuth() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardPointeServiceAuth();
        }
        return "";
    }

    public String getConnectUsername() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardConnectUsername();
        }
        return "";
    }

    public String getConnectPassword() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardConnectPassword();
        }
        return "";
    }

    public String getConnectServiceUrl() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardConnectServiceUrl();
        }
        return "";
    }

    public String getCardSecureServiceUrl() {
        SecretsHolder secretsHolder = getSecrets();
        if (secretsHolder != null) {
            return secretsHolder.getCardSecureServiceUrl();
        }
        return "";
    }
}
