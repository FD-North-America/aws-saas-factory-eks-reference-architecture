package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.converter.SalesHistoryConverter;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Setter
@Getter
@DynamoDBTable(tableName = TABLE_NAME)
public class SalesHistory {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String MONTH_AMOUNT_MAP = "MonthAmountMap";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String sortKey;

    @DynamoDBAttribute(attributeName = DbAttrNames.MONTH_AMOUNT_MAP)
    @DynamoDBTypeConverted(converter = SalesHistoryConverter.class)
    private Map<String, Float> monthAmountMap = new HashMap<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBIgnore
    public String getYear() {
        return sortKey.split(KEY_DELIMITER)[1];
    }

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.SALES_HISTORY.getLabel());
    }

    public static String buildSortKey(String productId) {
        return String.format("%s%s%s", productId, KEY_DELIMITER, LocalDate.now().getYear());
    }
}
