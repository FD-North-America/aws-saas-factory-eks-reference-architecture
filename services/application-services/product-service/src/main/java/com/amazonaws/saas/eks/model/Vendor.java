package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@DynamoDBTable(tableName = Vendor.TABLE_NAME)
public class Vendor {
    public static final String TABLE_NAME = "Product_v4";
    public static final String KEY_DELIMITER = "#";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Vendor.PARTITION_KEY)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = Vendor.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Vendor.NAME)
    private String name;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Vendor.DESCRIPTION)
    private String description;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Vendor.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Vendor.MODIFIED)
    private Date modified;
}
