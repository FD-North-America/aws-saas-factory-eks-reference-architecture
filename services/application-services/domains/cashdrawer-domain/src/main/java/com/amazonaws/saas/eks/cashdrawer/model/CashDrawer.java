package com.amazonaws.saas.eks.cashdrawer.model;

import com.amazonaws.saas.eks.cashdrawer.model.converter.CashDrawerTrayConverter;
import com.amazonaws.saas.eks.cashdrawer.model.converter.WorkstationIdConverter;
import com.amazonaws.saas.eks.cashdrawer.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName = CashDrawer.TABLE_NAME)
public class CashDrawer {
    public static final String TABLE_NAME = "CashDrawer";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NUMBER = "Number";
        public static final String DESCRIPTION = "Description";
        public static final String STATUS = "Status";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String AUTO_STARTUP = "AutoStartup";
        public static final String STARTUP_AMOUNT = "StartupAmount";
        public static final String ASSIGNED_USER = "AssignedUser";
        public static final String STARTUP_DATE = "StartupDate";
        public static final String STARTUP_REP = "StartupRep";
        public static final String CHECKOUT_DATE = "CheckoutDate";
        public static final String CHECKOUT_REP = "CheckoutRep";
        public static final String CHECKOUT_AMOUNTS = "CheckoutAmounts";
        public static final String CLEARED_DATE = "ClearedDate";
        public static final String CLEARED_BY = "ClearedBy";
        public static final String TRAYS = "Trays";
        public static final String TRAYS_TOTAL_AMOUNT = "TraysTotalAmount";
        public static final String CARD_TOTAL_AMOUNT = "CardTotalAmount";
        public static final String CASH_TOTAL_AMOUNT = "CashTotalAmount";
        public static final String WORKSTATION_IDS = "WorkStationIds";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String CASH_DRAWER_NUMBER_INDEX = "CashDrawerNumber-index"; // PK: PartitionKey, SK: Number
        public static final String CASH_DRAWER_ASSIGNED_USER_INDEX = "CashDrawerAssignedUser-index"; // PK: PartitionKey, SK: AssignedUser

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "cash-drawers-index";
        public static final String ENTITY = EntityType.CASHDRAWERS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String NUMBER = "NewImage.Number.S";
            public static final String STATUS = "NewImage.Status.S";
            public static final String DESCRIPTION = "NewImage.Description.S";
            public static final String ASSIGNED_USER = "NewImage.AssignedUser.S";

            public static final String SORT_NUMBER = "NewImage.Number.S.keyword";
            public static final String SORT_STATUS = "NewImage.Status.S.keyword";
            public static final String SORT_DESCRIPTION = "NewImage.Description.S.keyword";
            public static final String SORT_ASSIGNED_USER = "NewImage.AssignedUser.S.keyword";

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
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.CASH_DRAWER_NUMBER_INDEX, DbIndexNames.CASH_DRAWER_ASSIGNED_USER_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CASH_DRAWER_NUMBER_INDEX)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.DESCRIPTION)
    private String description;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBAttribute(attributeName = DbAttrNames.AUTO_STARTUP)
    private Boolean autoStartup;

    @DynamoDBAttribute(attributeName = DbAttrNames.STARTUP_AMOUNT)
    private BigDecimal startUpAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.ASSIGNED_USER)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CASH_DRAWER_ASSIGNED_USER_INDEX)
    private String assignedUser;

    @DynamoDBAttribute(attributeName = DbAttrNames.STARTUP_DATE)
    private Date startupDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.STARTUP_REP)
    private String startupRep;

    @DynamoDBAttribute(attributeName = DbAttrNames.CHECKOUT_DATE)
    private Date checkoutDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.CHECKOUT_REP)
    private String checkoutRep;

    @DynamoDBAttribute(attributeName = DbAttrNames.CHECKOUT_AMOUNTS)
    private BigDecimal checkoutAmounts;

    @DynamoDBAttribute(attributeName = DbAttrNames.CLEARED_DATE)
    private Date clearedDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.CLEARED_BY)
    private String clearedBy;

    @DynamoDBAttribute(attributeName = DbAttrNames.TRAYS)
    @DynamoDBTypeConverted(converter = CashDrawerTrayConverter.class)
    private List<CashDrawerTray> trays = new ArrayList<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.TRAYS_TOTAL_AMOUNT)
    private BigDecimal traysTotalAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_TOTAL_AMOUNT)
    private BigDecimal cashTotalAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CARD_TOTAL_AMOUNT)
    private BigDecimal cardTotalAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.WORKSTATION_IDS)
    @DynamoDBTypeConverted(converter = WorkstationIdConverter.class)
    private List<String> workstationIds;
}
