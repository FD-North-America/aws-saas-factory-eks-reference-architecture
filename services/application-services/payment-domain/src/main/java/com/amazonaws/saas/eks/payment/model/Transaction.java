package com.amazonaws.saas.eks.payment.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

@DynamoDBTable(tableName = Transaction.TABLE_NAME)
public class Transaction {
    public static final String TABLE_NAME = "Transaction";
    public static final String ATTR_PARTITION_KEY = "PartitionKey";
    public static final String ATTR_SORT_KEY = "SortKey";
    public static final String ATTR_MERCHANT_ID = "MerchantId";
    public static final String ATTR_HSN = "HSN";
    public static final String ATTR_TYPE = "Type";
    public static final String ATTR_REQUEST_BODY = "RequestBody";
    public static final String ATTR_STATUS = "Status";
    public static final String ATTR_RESPONSE_BODY = "ResponseBody";
    public static final String ATTR_REQUEST_DATE = "RequestDate";
    public static final String ATTR_RESPONSE_DATE = "ResponseDate";
    public static final String ATTR_ORDER_NUMBER = "OrderNumber";
    public static final String INDEX_ORDER_NUMBER = "OrderNumber-index"; // PK: Partition Key, SK: OrderNumber

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Transaction.ATTR_PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = INDEX_ORDER_NUMBER)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = Transaction.ATTR_SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_MERCHANT_ID)
    private String merchantId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_HSN)
    private String hsn;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_TYPE)
    private String type;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_REQUEST_BODY)
    private String requestBody;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_STATUS)
    private String status;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_RESPONSE_BODY)
    private String responseBody;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_REQUEST_DATE)
    private String requestDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_RESPONSE_DATE)
    private String responseDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Transaction.ATTR_ORDER_NUMBER)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = INDEX_ORDER_NUMBER)
    private String orderNumber;
}
