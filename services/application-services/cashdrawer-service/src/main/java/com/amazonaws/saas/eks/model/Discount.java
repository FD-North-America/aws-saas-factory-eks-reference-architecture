package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@DynamoDBTable(tableName = Discount.TABLE_NAME)
public class Discount {
    public static final String TABLE_NAME = "Order";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String REASON = "Reason";
    public static final String PRICE = "Price";
    public static final String ORDER_ID = "OrderId";
    public static final String PRODUCT_ID = "ProductId";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    public static final String ORDER_ID_INDEX = "OrderId-index";

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {ORDER_ID_INDEX})
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = REASON)
    private String reason;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = PRICE)
    private BigDecimal price;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = ORDER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = ORDER_ID_INDEX)
    private String orderId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = PRODUCT_ID)
    private String productId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = MODIFIED)
    private Date modified;
}
