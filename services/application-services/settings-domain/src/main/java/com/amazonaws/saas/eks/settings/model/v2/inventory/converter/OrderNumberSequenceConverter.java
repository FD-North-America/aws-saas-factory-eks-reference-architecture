package com.amazonaws.saas.eks.settings.model.v2.inventory.converter;

import com.amazonaws.saas.eks.settings.model.v2.inventory.OrderNumberSequence;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class OrderNumberSequenceConverter implements DynamoDBTypeConverter<String, OrderNumberSequence> {
    @Override
    public String convert(OrderNumberSequence orderNumberSequence) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(orderNumberSequence);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public OrderNumberSequence unconvert(String orderNumberSequence) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(orderNumberSequence, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
