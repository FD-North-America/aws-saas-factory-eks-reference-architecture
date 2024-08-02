package com.amazonaws.saas.eks.settings.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName = Settings.TABLE_NAME)
public class Settings {
    public static final String TABLE_NAME = "Settings";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String UNIT_OF_MEASURE_NAMES = "UnitOfMeasureNames";
    public static final String TAX_RATE = "TaxRate";
    public static final String MERCHANT_ID = "MerchantId";
    public static final String DEFAULT_HSN = "DefaultHSN";
    public static final String PRINTER_IP = "PrinterIP";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";
    public static final String TIMEZONE = "TimeZone";

    @DynamoDBHashKey(attributeName = Settings.PARTITION_KEY)
    private String partitionKey;

    @DynamoDBAttribute(attributeName = Settings.UNIT_OF_MEASURE_NAMES)
    private List<String> unitOfMeasureNames;

    @DynamoDBAttribute(attributeName = Settings.TAX_RATE)
    private BigDecimal taxRate;

    @DynamoDBAttribute(attributeName = Settings.MERCHANT_ID)
    private String merchantId;

    @DynamoDBAttribute(attributeName = Settings.DEFAULT_HSN)
    private String defaultHSN;

    @DynamoDBAttribute(attributeName = Settings.PRINTER_IP)
    private String printerIp;

    @DynamoDBAttribute(attributeName = Settings.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = Settings.MODIFIED)
    private Date modified;

    @DynamoDBAttribute(attributeName = Settings.TIMEZONE)
    private String timeZone;
}

