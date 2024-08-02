package com.amazonaws.saas.eks.product.model.vendor;

import com.amazonaws.saas.eks.product.model.converter.vendor.EDIConverter;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Getter
@Setter
@DynamoDBTable(tableName = TABLE_NAME)
public class Vendor {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String NUMBER = "Number";
        public static final String PAYEE_VENDOR_ID = "PayeeVendorId";
        public static final String EMAIL = "Email";
        public static final String PHONE1 = "Phone1";
        public static final String PHONE2 = "Phone2";
        public static final String FAX = "Fax";
        public static final String PHYSICAL_ADDRESS = "PhysicalAddress";
        public static final String PHYSICAL_CITY = "PhysicalCity";
        public static final String PHYSICAL_STATE = "PhysicalState";
        public static final String PHYSICAL_ZIP = "PhysicalZip";
        public static final String MAILING_ADDRESS = "MailingAddress";
        public static final String MAILING_CITY = "MailingCity";
        public static final String MAILING_STATE = "MailingState";
        public static final String MAILING_ZIP = "MailingZip";
        public static final String STATUS = "Status";
        public static final String EDI = "EDI";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String NUMBER_INDEX = "Number-index"; // PK: PartitionKey, SK: Number

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "vendors-index";
        public static final String ENTITY = EntityType.VENDORS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String NAME = "NewImage.Name.S";
            public static final String NUMBER = "NewImage.Number.S";
            public static final String PHONE1 = "NewImage.Phone1.S";

            public static final String PARTITION_KEY_KEYWORD = "NewImage.PartitionKey.S.keyword";
            public static final String NAME_KEYWORD = "NewImage.Name.S.keyword";
            public static final String NUMBER_KEYWORD = "NewImage.Number.S.keyword";
            public static final String PHONE_NUMBER_KEYWORD = "NewImage.Phone1.S.keyword";
            public static final String ADDRESS_KEYWORD = "NewImage.PhysicalAddress.S.keyword";
            public static final String CITY_KEYWORD = "NewImage.PhysicalCity.S.keyword";
            public static final String STATE_KEYWORD = "NewImage.PhysicalState.S.keyword";
            public static final String ZIP_KEYWORD = "NewImage.PhysicalZip.S.keyword";
            public static final String STATUS_KEYWORD = "NewImage.Status.S.keyword";

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

    public static class SortKeys {
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String ZIP = "zip";
        public static final String STATUS = "status";

        public static final String SORT_DESC = "desc";

        private SortKeys() {
            throw new IllegalStateException();
        }
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = DbIndexNames.NUMBER_INDEX)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.DESCRIPTION)
    private String description;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.NUMBER_INDEX)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.PAYEE_VENDOR_ID)
    private String payeeVendorId;

    @DynamoDBAttribute(attributeName = DbAttrNames.EMAIL)
    private String email;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHONE1)
    private String phone1;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHONE2)
    private String phone2;

    @DynamoDBAttribute(attributeName = DbAttrNames.FAX)
    private String faxNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHYSICAL_ADDRESS)
    private String physicalAddress;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHYSICAL_CITY)
    private String physicalCity;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHYSICAL_STATE)
    private String physicalState;

    @DynamoDBAttribute(attributeName = DbAttrNames.PHYSICAL_ZIP)
    private String physicalZip;

    @DynamoDBAttribute(attributeName = DbAttrNames.MAILING_ADDRESS)
    private String mailingAddress;

    @DynamoDBAttribute(attributeName = DbAttrNames.MAILING_CITY)
    private String mailingCity;

    @DynamoDBAttribute(attributeName = DbAttrNames.MAILING_STATE)
    private String mailingState;

    @DynamoDBAttribute(attributeName = DbAttrNames.MAILING_ZIP)
    private String mailingZip;

    @DynamoDBTypeConverted(converter = EDIConverter.class)
    @DynamoDBAttribute(attributeName = DbAttrNames.EDI)
    private EDI edi = new EDI();

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.VENDORS.getLabel());
    }
}
