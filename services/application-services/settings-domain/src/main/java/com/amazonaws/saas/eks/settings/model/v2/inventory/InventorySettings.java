package com.amazonaws.saas.eks.settings.model.v2.inventory;

import com.amazonaws.saas.eks.settings.model.v2.inventory.converter.OrderNumberSequenceConverter;
import com.amazonaws.saas.eks.settings.model.v2.inventory.converter.StoreLocationCodesConverter;
import com.amazonaws.saas.eks.settings.model.v2.inventory.converter.UnitsOfMeasureConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@DynamoDBTable(tableName = InventorySettings.TABLE_NAME)
public class InventorySettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String ORDER_NUMBER_SEQUENCE = "OrderNumberSequence";
        public static final String UNITS_OF_MEASURE = "UnitsOfMeasure";
        public static final String STORE_LOCATION_CODES = "StoreLocationCodes";
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
    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_NUMBER_SEQUENCE)
    @DynamoDBTypeConverted(converter = OrderNumberSequenceConverter.class)
    private OrderNumberSequence orderNumberSequence;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.UNITS_OF_MEASURE)
    @DynamoDBTypeConverted(converter = UnitsOfMeasureConverter.class)
    private List<Uom> unitsOfMeasure;

    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.STORE_LOCATION_CODES)
    @DynamoDBTypeConverted(converter = StoreLocationCodesConverter.class)
    private List<StoreLocationCode> storeLocationCodes;

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
