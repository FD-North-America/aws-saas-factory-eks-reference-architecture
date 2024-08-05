package com.amazonaws.saas.eks.order.model.converter;

import com.amazonaws.saas.eks.order.model.CategoryOrder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class CategoryOrderListConverter implements DynamoDBTypeConverter<String, List<CategoryOrder>> {
    @Override
    public String convert(List<CategoryOrder> categoryOrdersList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(categoryOrdersList);
        } catch (JsonProcessingException e)  {
            return null;
        }
    }

    @Override
    public List<CategoryOrder> unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<List<CategoryOrder>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
