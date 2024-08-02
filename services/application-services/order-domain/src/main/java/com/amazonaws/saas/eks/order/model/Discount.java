package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@DynamoDBTable(tableName = Discount.TABLE_NAME)
public class Discount {
    public static final String TABLE_NAME = "Order";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String REASON = "Reason";
        public static final String PRICE = "Price";
        public static final String CODE = "Code";
        public static final String ORDER_ID = "OrderId";
        public static final String ORDER_NUMBER = "OrderNumber";
        public static final String REP_USER = "RepUser";
        public static final String PRODUCT_ID = "ProductId";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String TYPE = "Type";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String ORDER_ID_INDEX = "OrderId-index"; // PK: PartitionKey, SK: OrderId

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "discounts-index";
        public static final String ENTITY = EntityType.DISCOUNTS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String PRICE = "NewImage.Price.N";
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
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.ORDER_ID_INDEX })
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.REASON)
    private String reason;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.PRICE)
    private BigDecimal price;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CODE)
    private String code;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.ORDER_ID_INDEX)
    private String orderId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_NUMBER)
    private String orderNumber;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.REP_USER)
    private String repUser;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.PRODUCT_ID)
    private String productId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.TYPE)
    private String type;
}
