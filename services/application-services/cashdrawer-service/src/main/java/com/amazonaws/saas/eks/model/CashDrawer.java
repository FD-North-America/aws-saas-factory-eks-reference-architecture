package com.amazonaws.saas.eks.model;

import com.amazonaws.saas.eks.model.converters.CashDrawerTrayConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = CashDrawer.TABLE_NAME)
public class CashDrawer {
    public static final String TABLE_NAME = "Order";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String NUMBER = "Number";
    public static final String DESCRIPTION = "Description";
    public static final String STATUS = "Status";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";
    public static final String AUTO_STARTUP = "AutoStartup";
    public static final String STARTUP_AMOUNT = "StartupAmount";
    public static final String ASSIGNED_USER = "AssignedUser";
    public static final String STARTUP_REP = "StartupRep";
    public static final String CHECKOUT_REP = "CheckoutRep";
    public static final String CHECKOUT_AMOUNTS = "CheckoutAmounts";
    public static final String CLEARED_DATE = "ClearedDate";
    public static final String CLEARED_BY = "ClearedBy";
    public static final String TRAYS = "Trays";
    public static final String STARTUP_DATE = "StartupDate";

    public static final String CASH_DRAWER_NUMBER_INDEX = "CashDrawerNumber-index"; // PK: PartitionKey, SK: Number

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = CashDrawer.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {CASH_DRAWER_NUMBER_INDEX})
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = CashDrawer.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.NUMBER)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = CASH_DRAWER_NUMBER_INDEX)
    private String number;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.DESCRIPTION)
    private String description;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.STATUS)
    private String status;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.MODIFIED)
    private Date modified;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.AUTO_STARTUP)
    private Boolean autoStartup;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.STARTUP_AMOUNT)
    private BigDecimal startUpAmount;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.STARTUP_DATE)
    private Date startupDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.STARTUP_REP)
    private String startupRep;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.ASSIGNED_USER)
    private String assignedUser;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.CHECKOUT_REP)
    private String checkoutRep;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.CHECKOUT_AMOUNTS)
    private BigDecimal checkoutAmounts;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.CLEARED_DATE)
    private Date clearedDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.CLEARED_BY)
    private String clearedBy;
    
    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = CashDrawer.TRAYS)
    @DynamoDBTypeConverted(converter = CashDrawerTrayConverter.class)
    private List<CashDrawerTray> trays = new ArrayList<>();
}
