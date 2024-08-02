package com.amazonaws.saas.eks.cashdrawer.model.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class WorkstationIdConverter implements DynamoDBTypeConverter<String, List<String>> {
    @Override
    public String convert(List<String> workstationIds) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(workstationIds);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<>() {
            });
        } catch (Exception e) {
            return null;
        }
    }
}
