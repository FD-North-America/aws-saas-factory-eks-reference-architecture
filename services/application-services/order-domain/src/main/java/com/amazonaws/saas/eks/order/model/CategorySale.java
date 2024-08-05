package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.converter.CategoryOrderListConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = CategorySale.TABLE_NAME)
public class CategorySale {
    public static final String TABLE_NAME = "Order";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String CATEGORY_ID = "CategoryId";
        public static final String ORDERS = "Orders";
        public static final String CREATED = "Created";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "category-sales-index";

        public static class FieldNames {
            public static final String CREATED = "NewImage.Created.S";

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

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String sortKey;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CATEGORY_ID)
    private String categoryId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.ORDERS)
    @DynamoDBTypeConverted(converter = CategoryOrderListConverter.class)
    private List<CategoryOrder> orders = new ArrayList<>();

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    public static String buildSortKey(String categoryId, Date created) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(created);
        String timeString = String.format("%s-%s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        return String.format("%s%s%s", categoryId, KEY_DELIMITER, timeString);
    }
}
