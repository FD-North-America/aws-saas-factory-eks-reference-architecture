package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@DynamoDBTable(tableName = VolumePricing.TABLE_NAME)
public class VolumePricing {
    public static final String TABLE_NAME = "Product_v4";
    public static final String KEY_DELIMITER = "#";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String BREAK_POINT_NAME = "BreakPointName";
    public static final String BREAK_POINT_QTY = "BreakPointQty";
    public static final String UOM_ID = "UomID";
    public static final String PRODUCT_ID = "ProductID";
    public static final String MODE = "Mode";
    public static final String DISCOUNT = "Discount";
    public static final String ACTIVE = "Active";
    public static final String FACTOR = "Factor";
    public static final String PRICE = "Price";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    public static final String PRODUCT_ID_INDEX = "ProductID-index"; // PK: PartitionKey, SK: ProductID
    public static final String UOM_ID_INDEX = "UomID-index"; // PK: PartitionKey, SK: UomID

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = VolumePricing.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { VolumePricing.PRODUCT_ID_INDEX, VolumePricing.UOM_ID_INDEX })
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = VolumePricing.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.BREAK_POINT_NAME)
    private String breakPointName;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.BREAK_POINT_QTY)
    private Integer breakPointQty;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.UOM_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = VolumePricing.UOM_ID_INDEX)
    private String uomId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.PRODUCT_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = VolumePricing.PRODUCT_ID_INDEX)
    private String productId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.MODE)
    private String mode;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.DISCOUNT)
    private BigDecimal discount;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.ACTIVE)
    private Boolean active;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.FACTOR)
    private Double factor;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.PRICE)
    private BigDecimal price;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = VolumePricing.MODIFIED)
    private Date modified;
}
