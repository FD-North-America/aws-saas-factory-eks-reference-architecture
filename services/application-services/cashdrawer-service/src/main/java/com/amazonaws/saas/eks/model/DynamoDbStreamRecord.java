package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamoDbStreamRecord implements Serializable {
    @Getter
    @Setter
    @JsonProperty("NewImage")
    private Map<String, AttributeValue> newImage;
}
