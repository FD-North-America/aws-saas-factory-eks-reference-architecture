package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Getter
@Setter
@DynamoDBTable(tableName = TABLE_NAME)
public class Counter {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String COUNT = "Count";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String sortKey;

    @DynamoDBAttribute(attributeName = DbAttrNames.COUNT)
    private int count;

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s", tenantId, KEY_DELIMITER, EntityType.COUNTER.getLabel());
    }
}
