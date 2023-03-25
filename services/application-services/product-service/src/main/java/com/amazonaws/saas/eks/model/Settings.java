package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

// TODO: MOVE TO SETTINGS SERVICE
@DynamoDBTable(tableName = Settings.TABLE_NAME)
public class Settings {
    public static final String TABLE_NAME = "Settings";
    public static final String KEY_DELIMITER = "#";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String UNIT_OF_MEASURE_NAMES = "UnitOfMeasureNames";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Settings.PARTITION_KEY)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Settings.UNIT_OF_MEASURE_NAMES)
    private List<String> unitOfMeasureNames;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Settings.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Settings.MODIFIED)
    private Date modified;
}
