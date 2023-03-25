package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@DynamoDBTable(tableName = UOM.TABLE_NAME)
public class UOM {
    public static final String TABLE_NAME = "Product_v4";
    public static final String KEY_DELIMITER = "#";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String NAME = "Name";
    public static final String FACTOR = "Factor";
    public static final String BARCODE = "Barcode";
    public static final String PRODUCT_ID = "ProductID";
    public static final String ALTERNATE_ID = "AlternateID";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    public static final String PRODUCT_ID_INDEX = "ProductID-index"; // PK: PartitionKey, SK: ProductID
    public static final String BARCODE_INDEX = "Barcode-index"; // PK: PartitionKey, SK: Barcode

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = UOM.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { UOM.PRODUCT_ID_INDEX, UOM.BARCODE_INDEX })
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = UOM.SORT_KEY)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = UOM.PRODUCT_ID_INDEX)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.NAME)
    private String name; // its value belongs to a pre-defined set by store (e.g. Store1 has {"Each", "Package"})

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.FACTOR)
    private Double factor;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.BARCODE)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = UOM.BARCODE_INDEX)
    private String barcode;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.PRODUCT_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = UOM.PRODUCT_ID_INDEX)
    private String productId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.ALTERNATE_ID)
    private String alternateId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = UOM.MODIFIED)
    private Date modified;
}
