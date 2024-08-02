package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.Transaction;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TransactionConverter implements DynamoDBTypeConverter<String, List<Transaction>> {
    @Override
    public String convert(List<Transaction> transactions) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(transactions);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<Transaction> unconvert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, new TypeReference<>() {
            });
        } catch (Exception e) {
            return null;
        }
    }
}
