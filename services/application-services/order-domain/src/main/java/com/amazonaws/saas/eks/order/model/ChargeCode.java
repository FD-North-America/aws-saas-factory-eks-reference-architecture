package com.amazonaws.saas.eks.order.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@DynamoDBTable(tableName = ChargeCode.TABLE_NAME)
public class ChargeCode {
    public static final String TABLE_NAME = "Order";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String CODE = "Code";
        public static final String AMOUNT = "Amount";
        public static final String ORDER_ID = "OrderId";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String ORDER_ID_INDEX = "OrderId-index"; // PK: PartitionKey, SK: OrderId

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.ORDER_ID_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.CODE)
    private String code;

    @DynamoDBAttribute(attributeName = DbAttrNames.AMOUNT)
    private BigDecimal amount;

    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.ORDER_ID_INDEX)
    private String orderId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;
}
