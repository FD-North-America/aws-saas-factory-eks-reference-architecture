package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.ReasonCodeItem;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ReasonCodesConverter implements DynamoDBTypeConverter<String, List<ReasonCodeItem>> {
    @Override
    public String convert(List<ReasonCodeItem> reasonCode) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(reasonCode);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<ReasonCodeItem> unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
