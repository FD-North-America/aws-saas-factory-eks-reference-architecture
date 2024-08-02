package com.amazonaws.saas.eks.product.model.converter.vendor;

import com.amazonaws.saas.eks.product.model.vendor.EDI;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EDIConverter implements DynamoDBTypeConverter<String, EDI> {

    @Override
    public String convert(EDI edi) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(edi);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public EDI unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, EDI.class);
        } catch (Exception e) {
            return null;
        }
    }
}
