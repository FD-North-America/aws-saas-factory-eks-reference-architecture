package com.amazonaws.saas.eks.secrets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Secrets {
    @JsonProperty(value = "EncryptionKey")
    private String encryptionKey;
}
