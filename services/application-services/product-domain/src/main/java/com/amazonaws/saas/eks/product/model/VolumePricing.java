package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Setter
@Getter
@DynamoDBTable(tableName = TABLE_NAME)
public class VolumePricing {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String BREAK_POINT_NAME = "BreakPointName";
        public static final String BREAK_POINT_QTY = "BreakPointQty";
        public static final String UOM_ID = "UomID";
        public static final String PRODUCT_ID = "ProductID";
        public static final String MODE = "Mode";
        public static final String DISCOUNT = "Discount";
        public static final String ACTIVE = "Active";
        public static final String FACTOR = "Factor";
        public static final String PRICE = "Price";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String PRODUCT_ID_INDEX = "ProductID-index"; // PK: PartitionKey, SK: ProductID
        public static final String UOM_ID_INDEX = "UomID-index"; // PK: PartitionKey, SK: UomID

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.PRODUCT_ID_INDEX, DbIndexNames.UOM_ID_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.BREAK_POINT_NAME)
    private String breakPointName;

    @DynamoDBAttribute(attributeName = DbAttrNames.BREAK_POINT_QTY)
    private Integer breakPointQty;

    @DynamoDBAttribute(attributeName = DbAttrNames.UOM_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.UOM_ID_INDEX)
    private String uomId;

    @DynamoDBAttribute(attributeName = DbAttrNames.PRODUCT_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.PRODUCT_ID_INDEX)
    private String productId;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODE)
    private String mode;

    @DynamoDBAttribute(attributeName = DbAttrNames.DISCOUNT)
    private BigDecimal discount;

    @DynamoDBAttribute(attributeName = DbAttrNames.ACTIVE)
    private Boolean active;

    @DynamoDBAttribute(attributeName = DbAttrNames.FACTOR)
    private Double factor;

    @DynamoDBAttribute(attributeName = DbAttrNames.PRICE)
    private BigDecimal price;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.VOLUME_PRICING.getLabel());
    }
}
