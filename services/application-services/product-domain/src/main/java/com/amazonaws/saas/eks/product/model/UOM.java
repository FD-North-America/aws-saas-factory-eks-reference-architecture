package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Setter
@Getter
@DynamoDBTable(tableName = TABLE_NAME)
public class UOM {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NAME = "Name";
        public static final String FACTOR = "Factor";
        public static final String BARCODE = "Barcode";
        public static final String PRODUCT_ID = "ProductID";
        public static final String ALTERNATE_ID = "AlternateID";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String PRODUCT_ID_INDEX = "ProductID-index"; // PK: PartitionKey, SK: ProductID
        public static final String BARCODE_INDEX = "Barcode-index"; // PK: PartitionKey, SK: Barcode

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "uom-index";
        public static final String ENTITY = EntityType.UOM.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String BARCODE = "NewImage.Barcode.S";
            public static final String ALTERNATE_ID = "NewImage.AlternateID.S";

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
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.PRODUCT_ID_INDEX, DbIndexNames.BARCODE_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.PRODUCT_ID_INDEX)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name; // its value belongs to a pre-defined set by store (e.g. Store1 has {"Each", "Package"})

    @DynamoDBAttribute(attributeName = DbAttrNames.FACTOR)
    private Double factor;

    @DynamoDBAttribute(attributeName = DbAttrNames.BARCODE)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.BARCODE_INDEX)
    private String barcode;

    @DynamoDBAttribute(attributeName = DbAttrNames.PRODUCT_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.PRODUCT_ID_INDEX)
    private String productId;

    @DynamoDBAttribute(attributeName = DbAttrNames.ALTERNATE_ID)
    private String alternateId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.UOM.getLabel());
    }
}
