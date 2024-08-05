package com.amazonaws.saas.eks.settings.model.v2.purchasing.converter;

import com.amazonaws.saas.eks.settings.model.v2.purchasing.PurchasingOptions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PurchasingOptionsConverter implements DynamoDBTypeConverter<String, PurchasingOptions> {
    @Override
    public String convert(PurchasingOptions options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public PurchasingOptions unconvert(String options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(options, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
