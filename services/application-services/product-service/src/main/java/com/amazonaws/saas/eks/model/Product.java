package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = Product.TABLE_NAME)
public class Product {

	public static final String TABLE_NAME = "Product_v4";
	public static final String KEY_DELIMITER = "#";
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

    public static final String CATEGORY_ID_PRODUCT_ID_INDEX = "CategoryID-ProductID-index"; // PK: CategoryID, SK: ProductID
    public static final String VENDOR_ID_PRODUCT_ID_INDEX = "VendorID-ProductID-index"; // PK: VendorID, SK: ProductID
    public static final String SKU_INDEX = "SKU-index"; // PK: PartitionKey, SK: SKU

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Product.PARTITION_KEY)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = Product.SORT_KEY)
    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = {Product.CATEGORY_ID_PRODUCT_ID_INDEX, Product.VENDOR_ID_PRODUCT_ID_INDEX})
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.NAME)
    private String name;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.DESCRIPTION)
    private String description;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.SKU)
    private String sku;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.TYPE)
    private String type;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.CATEGORY_ID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Product.CATEGORY_ID_PRODUCT_ID_INDEX)
    private String categoryId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.CATEGORY_NAME)
    private String categoryName;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.CATEGORY_PATH)
    private String categoryPath;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.QUANTITY_ON_HAND)
    private Float quantityOnHand;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.MIN_QTY_ON_HAND)
    private Float minQtyOnHand;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.MAX_QTY_ON_HAND)
    private Float maxQtyOnHand;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Product.RETAIL_PRICE)
	private BigDecimal retailPrice;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.COST)
    private BigDecimal cost;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.INVENTORY_STATUS)
    private String inventoryStatus;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.TAXABLE)
    private String taxable;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.RETURNS_ALLOWED)
    private Boolean returnsAllowed;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.AGE_VERIFICATION_REQUIRED)
    private Boolean ageVerificationRequired;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Product.VENDOR_ID)
	@DynamoDBIndexHashKey(globalSecondaryIndexName = Product.VENDOR_ID_PRODUCT_ID_INDEX)
	private String vendorId;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Product.VENDOR_NAME)
	private String vendorName;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.STOCKING_UOM_ID)
    private String stockingUomId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.QUANTITY_UOM_ID)
    private String quantityUomId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.PRICING_UOM_ID)
    private String pricingUomId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Product.MODIFIED)
    private Date modified;

    @Getter
    @Setter
    private List<UOM> unitsOfMeasure;
}
