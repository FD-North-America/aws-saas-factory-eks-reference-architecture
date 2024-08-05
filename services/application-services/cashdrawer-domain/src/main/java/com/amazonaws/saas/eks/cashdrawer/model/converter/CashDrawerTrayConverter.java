package com.amazonaws.saas.eks.cashdrawer.model.converter;

import com.amazonaws.saas.eks.cashdrawer.model.CashDrawerTray;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class CashDrawerTrayConverter implements DynamoDBTypeConverter<String, List<CashDrawerTray>> {
    @Override
    public String convert(List<CashDrawerTray> cashDrawerTrayList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(cashDrawerTrayList);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<CashDrawerTray> unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
