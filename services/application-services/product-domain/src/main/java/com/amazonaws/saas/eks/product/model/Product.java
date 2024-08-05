package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@DynamoDBTable(tableName = Product.TABLE_NAME)
public class Product {
	public static final String TABLE_NAME = "Product";
	public static final String KEY_DELIMITER = "#";
    public static final String STORE_ID = "store1";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String SKU = "SKU";
        public static final String CATEGORY_ID = "CategoryID";
        public static final String CATEGORY_NAME = "CategoryName";
        public static final String CATEGORY_PATH = "CategoryPath";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String QUANTITY_ON_HAND = "QuantityOnHand";
        public static final String MIN_QTY_ON_HAND = "MinQtyOnHand";
        public static final String MAX_QTY_ON_HAND = "MaxQtyOnHand";
        public static final String RETAIL_PRICE = "RetailPrice";
        public static final String COST = "Cost";
        public static final String INVENTORY_STATUS = "InventoryStatus";
        public static final String VENDOR_ID = "VendorID";
        public static final String VENDOR_NAME = "VendorName";
        public static final String TYPE = "Type";
        public static final String TAXABLE = "Taxable";
        public static final String RETURNS_ALLOWED = "ReturnsAllowed";
        public static final String AGE_VERIFICATION_REQUIRED = "AgeVerificationRequired";
        public static final String STOCKING_UOM_ID = "StockingUomID";
        public static final String QUANTITY_UOM_ID = "QuantityUomID";
        public static final String PRICING_UOM_ID = "PricingUomID";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String VENDOR_INDEX = "VendorID-index";
        public static final String SKU_INDEX = "SKU-index"; // PK: PartitionKey, SK: SKU

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "products-index";
        public static final String ENTITY = EntityType.PRODUCTS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String CATEGORY_ID = "NewImage.CategoryID.S";
            public static final String VENDOR_ID = "NewImage.VendorID.S";
            public static final String PRODUCT_ID = "NewImage.ProductID.S";
            public static final String NAME = "NewImage.Name.S";
            public static final String SKU = "NewImage.SKU.S";
            public static final String CATEGORY_NAME = "NewImage.CategoryName.S";
            public static final String VENDOR_NAME = "NewImage.VendorName.S";
            public static final String ITEM_STATUS = "NewImage.InventoryStatus.S";
            public static final String NAME_KEYWORD = "NewImage.Name.S.keyword";
            public static final String SKU_KEYWORD = "NewImage.SKU.S.keyword";
            public static final String QOH_KEYWORD = "NewImage.QuantityOnHand.N.keyword";
            public static final String RETAIL_PRICE_KEYWORD = "NewImage.RetailPrice.N.keyword";
            public static final String CATEGORY_NAME_KEYWORD = "NewImage.CategoryName.S.keyword";
            public static final String VENDOR_NAME_KEYWORD = "NewImage.VendorName.S.keyword";
            public static final String SORT_KEY = "NewImage.SortKey.S";
            public static final String PARTITION_KEY_KEYWORD = "NewImage.PartitionKey.S.keyword";
            public static final String CATEGORY_ID_KEYWORD = "NewImage.CategoryID.S.keyword";
            public static final String VENDOR_ID_KEYWORD = "NewImage.VendorID.S.keyword";

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
    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = { DbIndexNames.VENDOR_INDEX, DbIndexNames.SKU_INDEX})
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.DESCRIPTION)
    private String description;

    @DynamoDBAttribute(attributeName = DbAttrNames.SKU)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = DbIndexNames.SKU_INDEX)
    private String sku;

    @DynamoDBAttribute(attributeName = DbAttrNames.TYPE)
    private String type;

    @DynamoDBAttribute(attributeName = DbAttrNames.CATEGORY_ID)
    private String categoryId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CATEGORY_NAME)
    private String categoryName;

    @DynamoDBAttribute(attributeName = DbAttrNames.CATEGORY_PATH)
    private String categoryPath;

    @DynamoDBAttribute(attributeName = DbAttrNames.QUANTITY_ON_HAND)
    private Float quantityOnHand;

    @DynamoDBAttribute(attributeName = DbAttrNames.MIN_QTY_ON_HAND)
    private Float minQtyOnHand;

    @DynamoDBAttribute(attributeName = DbAttrNames.MAX_QTY_ON_HAND)
    private Float maxQtyOnHand;

	@DynamoDBAttribute(attributeName = DbAttrNames.RETAIL_PRICE)
	private BigDecimal retailPrice;

    @DynamoDBAttribute(attributeName = DbAttrNames.COST)
    private BigDecimal cost;

    @DynamoDBAttribute(attributeName = DbAttrNames.INVENTORY_STATUS)
    private String inventoryStatus;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAXABLE)
    private String taxable;

    @DynamoDBAttribute(attributeName = DbAttrNames.RETURNS_ALLOWED)
    private Boolean returnsAllowed;

    @DynamoDBAttribute(attributeName = DbAttrNames.AGE_VERIFICATION_REQUIRED)
    private Boolean ageVerificationRequired;

	@DynamoDBAttribute(attributeName = DbAttrNames.VENDOR_ID)
	@DynamoDBIndexHashKey(globalSecondaryIndexName = DbIndexNames.VENDOR_INDEX)
	private String vendorId;

	@DynamoDBAttribute(attributeName = DbAttrNames.VENDOR_NAME)
	private String vendorName;

    @DynamoDBAttribute(attributeName = DbAttrNames.STOCKING_UOM_ID)
    private String stockingUomId;

    @DynamoDBAttribute(attributeName = DbAttrNames.QUANTITY_UOM_ID)
    private String quantityUomId;

    @DynamoDBAttribute(attributeName = DbAttrNames.PRICING_UOM_ID)
    private String pricingUomId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    private List<UOM> unitsOfMeasure;

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.PRODUCTS.getLabel());
    }
}
