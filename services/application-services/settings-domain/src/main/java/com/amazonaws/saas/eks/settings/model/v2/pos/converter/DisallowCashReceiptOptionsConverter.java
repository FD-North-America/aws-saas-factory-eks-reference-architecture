package com.amazonaws.saas.eks.settings.model.v2.pos.converter;

import com.amazonaws.saas.eks.settings.model.v2.pos.DisallowCashReceiptOptions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class DisallowCashReceiptOptionsConverter implements DynamoDBTypeConverter<String, DisallowCashReceiptOptions> {
    @Override
    public String convert(DisallowCashReceiptOptions options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public DisallowCashReceiptOptions unconvert(String options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(options, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
