package com.amazonaws.saas.eks.product.model.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class SalesHistoryConverter implements DynamoDBTypeConverter<String, Map<String, Float>> {
    @Override
    public String convert(Map<String, Float> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Map<String, Float> unconvert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
