package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@DynamoDBTable(tableName = CashDrawerCheckout.TABLE_NAME)
public class CashDrawerCheckout {
    public static final String TABLE_NAME = "Order";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String CASH_DRAWER_ID = "CashDrawerId";
    public static final String STATUS = "Status";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";
    public static final String STARTUP_DATE = "StartupDate";
    public static final String CHECKOUT_REP = "CheckoutRep";
    public static final String CHECKOUT_AMOUNTS = "CheckoutAmounts";
    public static final String CLEARED_DATE = "ClearedDate";
    public static final String CLEARED_BY = "ClearedBy";

    public static final String CASH_DRAWER_ID_INDEX = "CashDrawerId-index"; // PK: PartitionKey, SK: CashDrawerId

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = CashDrawerCheckout.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {CASH_DRAWER_ID_INDEX})
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = CashDrawerCheckout.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CASH_DRAWER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = CASH_DRAWER_ID_INDEX)
    private String cashDrawerId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.STATUS)
    private String status;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.MODIFIED)
    private Date modified;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.STARTUP_DATE)
    private Date startupDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CHECKOUT_REP)
    private String checkoutRep;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CHECKOUT_AMOUNTS)
    private BigDecimal checkoutAmounts;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CLEARED_DATE)
    private Date clearedDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawerCheckout.CLEARED_BY)
    private String clearedBy;
}
