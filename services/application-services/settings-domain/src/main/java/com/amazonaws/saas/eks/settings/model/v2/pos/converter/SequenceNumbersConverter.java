package com.amazonaws.saas.eks.settings.model.v2.pos.converter;

import com.amazonaws.saas.eks.settings.model.v2.pos.SequenceNumber;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class SequenceNumbersConverter implements DynamoDBTypeConverter<String, List<SequenceNumber>> {
    @Override
    public String convert(List<SequenceNumber> sequenceNumbers) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(sequenceNumbers);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SequenceNumber> unconvert(String sequenceNumbers) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(sequenceNumbers, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
