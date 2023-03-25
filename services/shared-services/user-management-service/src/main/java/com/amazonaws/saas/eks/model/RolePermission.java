package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.Getter;
import lombok.Setter;


@DynamoDBDocument
public class RolePermission {
    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = "name")
    private String name;
}
