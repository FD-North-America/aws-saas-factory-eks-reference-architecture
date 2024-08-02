package com.amazonaws.saas.eks.cashdrawer.model;

import com.amazonaws.saas.eks.cashdrawer.model.converter.CashDrawerTrayConverter;
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
@DynamoDBTable(tableName = CashDrawerCheckout.TABLE_NAME)
public class CashDrawerCheckout {
    public static final String TABLE_NAME = "CashDrawer";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String CASH_DRAWER_ID = "CashDrawerId";
        public static final String CASH_DRAWER_NUMBER = "CashDrawerNumber";
        public static final String STATUS = "Status";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String STARTUP_DATE = "StartupDate";
        public static final String CHECKOUT_REP = "CheckoutRep";
        public static final String CHECKOUT_AMOUNTS = "CheckoutAmounts";
        public static final String CLEARED_DATE = "ClearedDate";
        public static final String CLEARED_BY = "ClearedBy";
        public static final String TRAYS = "Trays";
        public static final String TRAYS_TOTAL_AMOUNT = "TraysTotalAmount";
        public static final String STARTUP_AMOUNT = "StartupAmount";
        public static final String CARD_TOTAL_AMOUNT = "CardTotalAmount";
        public static final String CASH_TOTAL_AMOUNT = "CashTotalAmount";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String CASH_DRAWER_ID_INDEX = "CashDrawerId-index"; // PK: PartitionKey, SK: CashDrawerId
        public static final String CHECKOUT_REP_INDEX = "CheckoutRep-index"; // PK: PartitionKey, SK: CheckoutRep

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "cash-drawer-checkouts-index";
        public static final String ENTITY = EntityType.CASHDRAWERCHECKOUTS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String CASH_DRAWER_ID = "NewImage.CashDrawerId.S";
            public static final String CREATED = "NewImage.Created.S";
            public static final String STATUS = "NewImage.Status.S";
            public static final String CHECKOUT_REP = "NewImage.CheckoutRep.S";

            public static final String SORT_CREATED = "NewImage.Created.S";

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
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.CASH_DRAWER_ID_INDEX, DbIndexNames.CHECKOUT_REP_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_DRAWER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CASH_DRAWER_ID_INDEX)
    private String cashDrawerId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_DRAWER_NUMBER)
    private String cashDrawerNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBAttribute(attributeName = DbAttrNames.STARTUP_DATE)
    private Date startupDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.STARTUP_AMOUNT)
    private BigDecimal startUpAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CHECKOUT_REP)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CHECKOUT_REP_INDEX)
    private String checkoutRep;

    @DynamoDBAttribute(attributeName = DbAttrNames.CHECKOUT_AMOUNTS)
    private BigDecimal checkoutAmounts;

    @DynamoDBAttribute(attributeName = DbAttrNames.CLEARED_DATE)
    private Date clearedDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.CLEARED_BY)
    private String clearedBy;

    @DynamoDBAttribute(attributeName = CashDrawerCheckout.DbAttrNames.TRAYS)
    @DynamoDBTypeConverted(converter = CashDrawerTrayConverter.class)
    private List<CashDrawerTray> trays = new ArrayList<>();

    @DynamoDBAttribute(attributeName = CashDrawerCheckout.DbAttrNames.TRAYS_TOTAL_AMOUNT)
    private BigDecimal traysTotalAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_TOTAL_AMOUNT)
    private BigDecimal cashTotalAmount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CARD_TOTAL_AMOUNT)
    private BigDecimal cardTotalAmount;
}
