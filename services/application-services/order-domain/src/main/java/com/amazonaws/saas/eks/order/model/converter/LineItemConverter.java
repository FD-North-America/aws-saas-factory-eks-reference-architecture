package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.LineItem;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;

import java.util.List;

public class LineItemConverter implements DynamoDBTypeConverter<String, List<LineItem>> {
    @Override
    public String convert(List<LineItem> objects) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<LineItem> unconvert(String objectssString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectssString, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
