package com.amazonaws.saas.eks.settings.model.v2.pos.converter;

import com.amazonaws.saas.eks.settings.model.v2.pos.PickingTicketPrintOptions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PickingTicketPrintOptionsConverter implements DynamoDBTypeConverter<String, PickingTicketPrintOptions> {
    @Override
    public String convert(PickingTicketPrintOptions options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public PickingTicketPrintOptions unconvert(String options) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(options, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
