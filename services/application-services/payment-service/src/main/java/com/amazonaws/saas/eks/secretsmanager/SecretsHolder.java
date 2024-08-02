package com.amazonaws.saas.eks.secretsmanager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecretsHolder {
    @JsonProperty(value = "CardPointeServiceUrlDev")
    private String cardPointeServiceUrl;

    @JsonProperty(value = "CardPointeServiceAuthDev")
    private String cardPointeServiceAuth;

    @JsonProperty(value = "EncryptionKeyDev")
    private String encryptionKey;

    @JsonProperty(value = "CardConnectServiceUrlDev")
    private String cardConnectServiceUrl;

    @JsonProperty(value = "CardConnectUsernameDev")
    private String cardConnectUsername;

    @JsonProperty(value = "CardConnectPasswordDev")
    private String cardConnectPassword;

    @JsonProperty(value = "CardSecureServiceUrlDev")
    private String cardSecureServiceUrl;
}
