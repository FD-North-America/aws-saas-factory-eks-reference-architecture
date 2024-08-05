package com.amazonaws.saas.eks.settings.model.v2.salestax;

import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@DynamoDBTable(tableName = SalesTaxSettings.TABLE_NAME)
public class SalesTaxSettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String CODE = "Code";
        public static final String DESCRIPTION = "Description";
        public static final String RATE = "Rate";
        public static final String TAXABLE_LIMIT = "TaxableLimit";
        public static final String TAXING_STATE = "TaxingState";
        public static final String JURISDICTION = "Jurisdiction";
        public static final String SALES_TAX_PATH = "SalesTaxPath";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String SALES_TAX_PATH_INDEX = "SalesTaxPath-index";

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.CODE)
    private String code;

    @DynamoDBAttribute(attributeName = DbAttrNames.DESCRIPTION)
    private String description;

    @DynamoDBAttribute(attributeName = DbAttrNames.RATE)
    private Float rate;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAXABLE_LIMIT)
    private BigDecimal taxableLimit;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAXING_STATE)
    private String taxingState;

    @DynamoDBAttribute(attributeName = DbAttrNames.JURISDICTION)
    private String jurisdiction;

    @DynamoDBAttribute(attributeName = DbAttrNames.SALES_TAX_PATH)
    private String salesTaxPath;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBIgnore
    private List<SalesTaxSettings> salesTaxes = new ArrayList<>();

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s", tenantId, KEY_DELIMITER, EntityType.SALES_TAX);
    }
}
