package com.amazonaws.saas.eks.settings.model.v2.accountsreceivable;

import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.converter.CustomerTypesConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@DynamoDBTable(tableName = AccountsReceivableSettings.TABLE_NAME)
public class AccountsReceivableSettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";

        public static final String SORT_KEY = "SortKey";

        public static final String CUSTOMER_TYPES = "CustomerTypes";

        public static final String CREATED = "Created";

        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @Setter
    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @Setter
    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_TYPES)
    @DynamoDBTypeConverted(converter = CustomerTypesConverter.class)
    private List<CustomerType> customerTypes;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return tenantId;
    }
}
