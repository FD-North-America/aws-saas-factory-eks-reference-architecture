package com.amazonaws.saas.eks.settings.model.v2.reasoncodes;

import com.amazonaws.saas.eks.settings.model.v2.reasoncodes.converter.ReasonCodesConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@DynamoDBTable(tableName = ReasonCodesSettings.TABLE_NAME)
public class ReasonCodesSettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String REASON_CODES = "ReasonCodes";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.REASON_CODES)
    @DynamoDBTypeConverted(converter = ReasonCodesConverter.class)
    private List<ReasonCode> reasonCodes;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return tenantId;
    }
}
