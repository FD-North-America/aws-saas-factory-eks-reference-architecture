package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.DeliveryAddressLine;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryAddressLineConverter implements DynamoDBTypeConverter<String, DeliveryAddressLine> {
    @Override
    public String convert(DeliveryAddressLine deliveryAddressLine) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(deliveryAddressLine);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public DeliveryAddressLine unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
