package com.amazonaws.saas.eks.model.converters;

import com.amazonaws.saas.eks.model.Transaction;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TransactionConverter implements DynamoDBTypeConverter<String, List<Transaction>> {
    @Override
    public String convert(List<Transaction> transactions) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(transactions);
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    @Override
    public List<Transaction> unconvert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, new TypeReference<List<Transaction>>() {
            });
        } catch (JsonMappingException e) {
        } catch (JsonProcessingException e) {
        }
        return null;
    }
}
