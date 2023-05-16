package com.amazonaws.saas.eks.secretsmanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class SecretsHolder {
    @Getter
    @Setter
    @JsonProperty(value = "CardPointeServiceUrlDev")
    private String cardPointeServiceUrl;

    @Getter
    @Setter
    @JsonProperty(value = "CardPointeServiceAuthDev")
    private String cardPointeServiceAuth;

    @Getter
    @Setter
    @JsonProperty(value = "EncryptionKeyDev")
    private String encryptionKey;
}
