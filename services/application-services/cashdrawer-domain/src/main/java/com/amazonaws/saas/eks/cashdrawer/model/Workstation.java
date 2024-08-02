package com.amazonaws.saas.eks.cashdrawer.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@DynamoDBTable(tableName = Workstation.TABLE_NAME)
public class Workstation {
    public static final String TABLE_NAME = "CashDrawer";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NUMBER = "Number";
        public static final String NAME = "Name";
        public static final String IP_ADDRESS = "IPAddress";
        public static final String HSN = "HSN";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.IP_ADDRESS)
    private String ipAddress;

    @DynamoDBAttribute(attributeName = DbAttrNames.HSN)
    private String hsn;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;
}
