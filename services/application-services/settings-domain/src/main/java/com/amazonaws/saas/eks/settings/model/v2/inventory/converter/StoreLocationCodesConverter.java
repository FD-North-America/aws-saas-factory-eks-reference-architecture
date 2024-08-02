package com.amazonaws.saas.eks.settings.model.v2.inventory.converter;

import com.amazonaws.saas.eks.settings.model.v2.inventory.StoreLocationCode;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class StoreLocationCodesConverter implements DynamoDBTypeConverter<String, List<StoreLocationCode>> {
    @Override
    public String convert(List<StoreLocationCode> storeLocationCodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(storeLocationCodes);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<StoreLocationCode> unconvert(String storeLocationCodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(storeLocationCodes, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
