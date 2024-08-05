package com.amazonaws.saas.eks.customer.model;

import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@DynamoDBTable(tableName = Account.TABLE_NAME)
public class Account {
    public static final String TABLE_NAME = "Customer";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NAME = "Name";
        public static final String LOYALTY_NUMBER = "LoyaltyNumber";
        public static final String STATUS = "Status";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String NUMBER = "Number";
        public static final String CREDIT_LIMIT = "CreditLimit";
        public static final String BALANCE = "Balance";
        public static final String IS_MAIN = "IsMain";
        public static final String CUSTOMER_ID = "CustomerID";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "accounts-index";
        public static final String ENTITY = EntityType.ACCOUNTS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String STATUS_KEYWORD = "NewImage.Status.S.keyword";
            public static final String CUSTOMER_ID_KEYWORD = "NewImage.CustomerID.S.keyword";
            public static final String NAME = "NewImage.Name.S";
            public static final String NUMBER = "NewImage.Number.S";
            public static final String NUMBER_KEYWORD = "NewImage.Number.S.keyword";
            public static final String NAME_KEYWORD = "NewImage.Name.S.keyword";
            public static final String CREDIT_LIMIT_KEYWORD = "NewImage.CreditLimit.N.keyword";
            public static final String BALANCE_KEYWORD = "NewImage.Balance.N.keyword";

            private FieldNames() {
                throw new IllegalStateException();
            }
        }

        private OpenSearch() {
            throw new IllegalStateException();
        }

        public static String getIndex(String tenantId) {
            return String.format("%s-%s", tenantId, INDEX);
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.LOYALTY_NUMBER)
    private String loyaltyNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREDIT_LIMIT)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.BALANCE)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @DynamoDBAttribute(attributeName = DbAttrNames.IS_MAIN)
    private Boolean isMain;

    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_ID)
    private String customerId;
}
