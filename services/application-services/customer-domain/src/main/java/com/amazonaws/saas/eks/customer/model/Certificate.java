package com.amazonaws.saas.eks.customer.model;

import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@DynamoDBTable(tableName = Certificate.TABLE_NAME)
public class Certificate {
    public static final String TABLE_NAME = "Customer";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NUMBER = "Number";
        public static final String NAME = "Name";
        public static final String EXPIRY_DATE = "ExpiryDate";
        public static final String CUSTOMER_ID = "CustomerID";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String CUSTOMER_ID_INDEX = "CustomerID-index"; // PK: PartitionKey, SK: OrderId

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.CUSTOMER_ID_INDEX })
    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.EXPIRY_DATE)
    private Date expiryDate;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CUSTOMER_ID_INDEX)
    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_ID)
    private String customerId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return EntityType.CERTIFICATES.getLabel();
    }
}
