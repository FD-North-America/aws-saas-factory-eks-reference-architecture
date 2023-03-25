package com.amazonaws.saas.eks.converter;

import java.io.IOException;
import java.util.List;

import com.amazonaws.saas.eks.model.RolePermission;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


public class ListOfRolePermissionConverter implements DynamoDBTypeConverter<String, List<RolePermission>> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writer();
    
    @Override
    public String convert(List<RolePermission> objects) {
        try {
            String objectsAsString = writer.writeValueAsString(objects);
            return objectsAsString;
        } catch (JsonProcessingException e) {
            // throw new Exception("Unable to marshall the instance of " + objects.getClass() + "into a string");
        }
        return null;
    }

    @Override
    public List<RolePermission> unconvert(String objectsAsString) {
        try {
            List<RolePermission> objects = mapper.readValue(objectsAsString,
                    new TypeReference<List<RolePermission>>() {
                    });
            return objects;
        } catch (JsonParseException e) {
            // do something
        } catch (JsonMappingException e) {
            // do something
        } catch (IOException e) {
            // do something
        }
        return null;
    }
}
