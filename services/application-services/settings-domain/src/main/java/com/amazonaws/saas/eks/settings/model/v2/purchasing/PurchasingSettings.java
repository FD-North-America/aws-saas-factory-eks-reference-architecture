package com.amazonaws.saas.eks.settings.model.v2.purchasing;

import com.amazonaws.saas.eks.settings.model.v2.purchasing.converter.PurchasingOptionsConverter;
import com.amazonaws.saas.eks.settings.model.v2.purchasing.converter.ReceivingOptionsConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@DynamoDBTable(tableName = PurchasingSettings.TABLE_NAME)
public class PurchasingSettings {
    public static final String TABLE_NAME = "Settings_v2";
    public static final String KEY_DELIMITER = "#";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";

        public static final String SORT_KEY = "SortKey";

        public static final String COMPANY_NAME = "CompanyName";

        public static final String BRANCH = "Branch";

        public static final String ADDRESS = "Address";

        public static final String CONTACT = "Contact";

        public static final String STATE = "State";

        public static final String CITY = "City";

        public static final String COUNTY = "County";

        public static final String PHONE = "Phone";

        public static final String ZIP = "Zip";

        public static final String FAX = "Fax";

        public static final String ORDER_NUMBER_FORMAT = "OrderNumberFormat";

        public static final String PURCHASING_OPTIONS = "PurchasingOptions";

        public static final String RECEIVING_OPTIONS = "ReceivingOptions";

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

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.COMPANY_NAME)
    private String companyName;

    @DynamoDBAttribute(attributeName = DbAttrNames.BRANCH)
    private String branch;

    @DynamoDBAttribute(attributeName = DbAttrNames.ADDRESS)
    private String address;

    @DynamoDBAttribute(attributeName = DbAttrNames.CONTACT)
    private String contact;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATE)
    private String state;

    @DynamoDBAttribute(attributeName = DbAttrNames.CITY)
    private String city;

    @DynamoDBAttribute(attributeName = DbAttrNames.COUNTY)
    private String county;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHONE)
    private String phone;

    @DynamoDBAttribute(attributeName = DbAttrNames.ZIP)
    private String zip;

    @DynamoDBAttribute(attributeName = DbAttrNames.FAX)
    private String fax;

    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_NUMBER_FORMAT)
    private String orderNumberFormat;

    @DynamoDBAttribute(attributeName = DbAttrNames.PURCHASING_OPTIONS)
    @DynamoDBTypeConverted(converter = PurchasingOptionsConverter.class)
    private PurchasingOptions purchasingOptions;

    @DynamoDBAttribute(attributeName = DbAttrNames.RECEIVING_OPTIONS)
    @DynamoDBTypeConverted(converter = ReceivingOptionsConverter.class)
    private ReceivingOptions receivingOptions;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return tenantId;
    }
}
