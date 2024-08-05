package com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.converter;

import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.CustomerType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class CustomerTypesConverter implements DynamoDBTypeConverter<String, List<CustomerType>> {
    @Override
    public String convert(List<CustomerType> customerTypes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(customerTypes);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<CustomerType> unconvert(String customerTypes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(customerTypes, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
