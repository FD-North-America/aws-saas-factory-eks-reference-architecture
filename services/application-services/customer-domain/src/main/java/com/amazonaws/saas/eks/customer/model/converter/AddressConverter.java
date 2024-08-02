package com.amazonaws.saas.eks.customer.model.converter;

import com.amazonaws.saas.eks.customer.model.Address;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddressConverter implements DynamoDBTypeConverter<String, Address> {
    @Override
    public String convert(Address address) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Address unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
