package com.amazonaws.saas.eks.settings.model.v2.reasoncodes.converter;

import com.amazonaws.saas.eks.settings.model.v2.reasoncodes.ReasonCode;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class ReasonCodesConverter implements DynamoDBTypeConverter<String, List<ReasonCode>> {
    @Override
    public String convert(List<ReasonCode> reasonCodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(reasonCodes);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<ReasonCode> unconvert(String reasonCodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(reasonCodes, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
