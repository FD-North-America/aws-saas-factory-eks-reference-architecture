package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.converter.DeliveryAddressLineConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@DynamoDBTable(tableName = Delivery.TABLE_NAME)
public class Delivery {
    public static final String TABLE_NAME = "Order";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String ORDER_ID = "OrderID";
        public static final String CONTACT_NAME = "ContactName";
        public static final String CONTACT_PHONE = "ContactPhone";
        public static final String REQUESTED_SHIP_DATE = "RequestedShipDate";
        public static final String DELIVERY_INSTRUCTIONS = "DeliveryInstructions";
        public static final String STREET_ADDRESS = "StreetAddress";
        public static final String CITY = "City";
        public static final String STATE = "State";
        public static final String ZIP = "Zip";
        public static final String COUNTRY = "Country";
        public static final String OUTSIDE_CITY_LIMITS = "OutsideCityLimits";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String STATUS = "Status";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_ID)
    private String orderId;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CONTACT_NAME)
    private String contactName;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CONTACT_PHONE)
    private String contactPhone;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.REQUESTED_SHIP_DATE)
    private Date requestedDeliveryDate;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.DELIVERY_INSTRUCTIONS)
    private String deliveryInstructions;


    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.STREET_ADDRESS)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine streetAddress;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.CITY)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine city;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.COUNTRY)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine country;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.STATE)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine state;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.ZIP)
    @DynamoDBTypeConverted(converter = DeliveryAddressLineConverter.class)
    private DeliveryAddressLine zip;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = DbAttrNames.OUTSIDE_CITY_LIMITS)
    private boolean outsideCityLimits;

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
    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;
}
