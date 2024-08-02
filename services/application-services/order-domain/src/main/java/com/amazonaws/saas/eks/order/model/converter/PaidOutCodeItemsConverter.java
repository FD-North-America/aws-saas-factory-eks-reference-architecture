package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.PaidOutCodeItem;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class PaidOutCodeItemsConverter implements DynamoDBTypeConverter<String, List<PaidOutCodeItem>> {
    @Override
    public String convert(List<PaidOutCodeItem> objects) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<PaidOutCodeItem> unconvert(String objectssString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectssString, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
