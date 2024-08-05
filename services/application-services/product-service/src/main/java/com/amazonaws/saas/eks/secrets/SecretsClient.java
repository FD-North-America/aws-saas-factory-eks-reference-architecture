package com.amazonaws.saas.eks.secrets;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.*;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecretsClient {
    private static final Logger logger = LogManager.getLogger(SecretsClient.class);

    private final String secretName;

    private final AWSSecretsManager manager;

    private final ObjectMapper objectMapper;

    public SecretsClient(@Value("${aws.secret.name}") final String secretPath,
                         final AWSSecretsManager manager,
                         final ObjectMapper objectMapper) {
        this.secretName = secretPath;
        this.manager = manager;
        this.objectMapper = objectMapper;
    }

    public String getEncryptionKey() {
        return getSecrets().map(Secrets::getEncryptionKey).orElse(null);
    }

    private Optional<Secrets> getSecrets() {
        String secretValue = null;
        Optional<Secrets> secrets = Optional.empty();
        try {
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId(secretName);
            GetSecretValueResult getSecretValueResult = manager.getSecretValue(getSecretValueRequest);
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
            try {
                secrets = Optional.ofNullable(objectMapper.readValue(secretValue, Secrets.class));
            } catch (JsonProcessingException e) {
                logger.error(String.format("An error occurred while mapping the secret value %s: %s", secretValue, e));
            }
        }

        return secrets;
    }
}
