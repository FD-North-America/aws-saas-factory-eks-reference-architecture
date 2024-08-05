package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.converter.DeliveryAddressLineConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@DynamoDBTable(tableName = Tax.TABLE_NAME)
public class Tax {
    public static final String TABLE_NAME = "Order";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String ORDER_ID = "OrderId";
        public static final String TYPE = "Type";
        public static final String EXEMPT_CODE = "ExemptCode";
        public static final String CERTIFICATE_ID = "CertificateId";
        public static final String STREET_ADDRESS = "StreetAddress";
        public static final String CITY = "City";
        public static final String COUNTY = "County";
        public static final String STATE = "State";
        public static final String ZIP = "Zip";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

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

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.ORDER_ID_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.ORDER_ID_INDEX)
    private String orderId;

    @DynamoDBAttribute(attributeName = DbAttrNames.TYPE)
    private String type;

    @DynamoDBAttribute(attributeName = DbAttrNames.EXEMPT_CODE)
    private String exemptCode;

    @DynamoDBAttribute(attributeName = DbAttrNames.CERTIFICATE_ID)
    private String certificateId;

    @DynamoDBAttribute(attributeName = DbAttrNames.STREET_ADDRESS)
    private String streetAddress;

    @DynamoDBAttribute(attributeName = DbAttrNames.CITY)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine city;

    @DynamoDBAttribute(attributeName = DbAttrNames.COUNTY)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine county;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATE)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine state;

    @DynamoDBAttribute(attributeName = DbAttrNames.ZIP)
    private String zip;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;
}
