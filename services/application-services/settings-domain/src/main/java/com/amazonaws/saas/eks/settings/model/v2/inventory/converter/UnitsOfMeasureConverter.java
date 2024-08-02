package com.amazonaws.saas.eks.settings.model.v2.inventory.converter;

import com.amazonaws.saas.eks.settings.model.v2.inventory.Uom;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class UnitsOfMeasureConverter implements DynamoDBTypeConverter<String, List<Uom>> {
    @Override
    public String convert(List<Uom> unitsOfMeasure) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(unitsOfMeasure);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<Uom> unconvert(String unitsOfMeasure) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(unitsOfMeasure, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
