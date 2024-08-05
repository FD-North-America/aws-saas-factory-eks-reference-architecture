package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.converter.LineItemConverter;
import com.amazonaws.saas.eks.order.model.converter.PaidOutCodeItemsConverter;
import com.amazonaws.saas.eks.order.model.converter.ReasonCodesConverter;
import com.amazonaws.saas.eks.order.model.converter.TransactionConverter;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@DynamoDBTable(tableName = Order.TABLE_NAME)
public class Order {
    public static final String TABLE_NAME = "Order";

    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NUMBER = "Number";
        public static final String STATUS = "Status";
        public static final String PAYMENT_TYPE = "PaymentType";
        public static final String TOTAL = "Total";
        public static final String SUB_TOTAL = "SubTotal";
        public static final String TAX_TOTAL = "TaxTotal";
        public static final String DISCOUNT_TOTAL = "DiscountTotal";
        public static final String TAXABLE_SUB_TOTAL = "TaxableSubTotal";
        public static final String NON_TAXABLE_SUB_TOTAL = "NonTaxableSubTotal";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";
        public static final String LINE_ITEMS = "LineItems";
        public static final String CASH_DRAWER_ID = "CashDrawerId";
        public static final String CASH_PAYMENT_AMOUNT = "CashPaymentAmount";
        public static final String CREDIT_PAYMENT_AMOUNT = "CreditPaymentAmount";
        public static final String BALANCE_DUE = "BalanceDue";
        public static final String TRANSACTIONS = "Transactions";
        public static final String PAID_DATE = "PaidDate";
        public static final String HAS_RETURNS = "HasReturns";
        public static final String PAID_OUT_CODE_ITEMS = "PaidOutCodeItems";
        public static final String REASON_CODES = "ReasonCodes";
        public static final String LINKED_ORDER_ID = "LinkedOrderId";
        public static final String TOTAL_COST = "TotalCost";
        public static final String PROFIT = "Profit";
        public static final String MARGIN = "Margin";
        public static final String PAID_OUT_TOTAL = "PaidOutTotal";
        public static final String CARD_TRANSACTION_COUNT = "CardTransactionCount";
        public static final String CASH_TRANSACTION_COUNT = "CashTransactionCount";
        public static final String SALES_REP = "SalesRep";
        public static final String RETURN_NON_TAXABLE_SUB_TOTAL = "ReturnNonTaxableSubTotal";
        public static final String RETURN_TAXABLE_SUB_TOTAL = "ReturnTaxableSubTotal";
        public static final String RETURN_TOTAL = "ReturnTotal";
        public static final String RETURN_TAX_TOTAL = "ReturnTaxTotal";
        public static final String CUSTOMER_ID = "CustomerId";
        public static final String CUSTOMER_NAME = "CustomerName";
        public static final String CUSTOMER_NUMBER = "CustomerNumber";
        public static final String ACCOUNT_NAME = "AccountName";
        public static final String ACCOUNT_NUMBER = "AccountNumber";
        public static final String IDENTIFIER = "Identifier";
        public static final String BUYER = "Buyer";
        public static final String BALANCE = "Balance";
        public static final String CREDIT_LIMIT = "CreditLimit";
        public static final String TYPE = "Type";
        public static final String PO_NUMBER = "PONumber";
        public static final String TENDERED = "Tendered";
        public static final String DELIVERED = "Delivered";
        public static final String EXPIRATION_DATE = "ExpirationDate";
        public static final String DELIVERY_ID = "DeliveryId";
        public static final String TAX_ID = "TaxId";
        public static final String FAILED_TRANSACTION_COUNT = "FailedTransactionCount";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String CASH_DRAWER_ID_INDEX = "CashDrawerId-index"; // PK: PartitionKey, SK: CashDrawerId

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public static class OpenSearch {
        private static final String INDEX = "orders-index";
        public static final String ENTITY = EntityType.ORDERS.getLabel();

        public static class FieldNames {
            public static final String PARTITION_KEY = "NewImage.PartitionKey.S";
            public static final String CASH_DRAWER_ID = "NewImage.CashDrawerId.S";
            public static final String STATUS = "NewImage.Status.S";
            public static final String CREATED_DATE = "NewImage.Created.S";
            public static final String PAID_DATE = "NewImage.PaidDate.S";
            public static final String NUMBER = "NewImage.Number.S";
            public static final String CUSTOMER_NUMBER = "NewImage.CustomerNumber.S";
            public static final String CUSTOMER_NAME = "NewImage.CustomerName.S";
            public static final String IDENTIFIER = "NewImage.Identifier.S";
            public static final String ACCOUNT_NAME = "NewImage.AccountName.S";
            public static final String PO_NUMBER = "NewImage.PONumber.S";
            public static final String HAS_RETURNS = "NewImage.HasReturns.N";
            public static final String SUBTOTAL_KEYWORD = "NewImage.SubTotal.N.keyword";
            public static final String NON_TAXABLE_SUB_TOTAL_KEYWORD ="NewImage.NonTaxableSubTotal.N.keyword";
            public static final String TAXABLE_SUB_TOTAL_KEYWORD = "NewImage.TaxableSubTotal.N.keyword";
            public static final String TAX_TOTAL_KEYWORD = "NewImage.TaxTotal.N.keyword";
            public static final String SALES_REP = "NewImage.SalesRep.S";
            public static final String NUMBER_KEYWORD = "NewImage.Number.S.keyword";
            public static final String TOTAL_KEYWORD = "NewImage.Total.N.keyword";
            public static final String CARD_TRANSACTION_COUNT_KEYWORD = "NewImage.CardTransactionCount.N.keyword";
            public static final String CASH_TRANSACTION_COUNT_KEYWORD = "NewImage.CashTransactionCount.N.keyword";
            public static final String PROFIT_KEYWORD = "NewImage.Profit.N.keyword";
            public static final String MARGIN_KEYWORD = "NewImage.Margin.N.keyword";
            public static final String DISCOUNT_TOTAL_KEYWORD = "NewImage.DiscountTotal.N.keyword";
            public static final String PAID_OUT_TOTAL_KEYWORD = "NewImage.PaidOutTotal.N.keyword";
            public static final String RETURN_NON_TAXABLE_SUB_TOTAL_KEYWORD = "NewImage.ReturnNonTaxableSubTotal.N.keyword";
            public static final String RETURN_TAXABLE_SUB_TOTAL_KEYWORD = "NewImage.ReturnTaxableSubTotal.N.keyword";
            public static final String RETURN_TOTAL_KEYWORD = "NewImage.ReturnTotal.N.keyword";
            public static final String RETURN_TAX_TOTAL_KEYWORD = "NewImage.ReturnTaxTotal.N.keyword";
            public static final String STATUS_KEYWORD = "NewImage.Status.S.keyword";
            public static final String TYPE_KEYWORD = "NewImage.Type.S.keyword";
            public static final String CUSTOMER_NAME_KEYWORD = "NewImage.CustomerName.S.keyword";
            public static final String CUSTOMER_NUMBER_KEYWORD = "NewImage.CustomerNumber.S.keyword";
            public static final String IDENTIFIER_KEYWORD = "NewImage.Identifier.S.keyword";
            public static final String ACCOUNT_NAME_KEYWORD = "NewImage.AccountName.S.keyword";
            public static final String PO_NUMBER_KEYWORD = "NewImage.PONumber.S.keyword";
            public static final String BALANCE_DUE_KEYWORD = "NewImage.BalanceDue.N.keyword";
            public static final String CREATED_KEYWORD = "NewImage.Created.S.keyword";
            public static final String TENDERED_KEYWORD = "NewImage.Tendered.N.keyword";
            public static final String EXPIRATION_DATE_KEYWORD = "NewImage.ExpirationDate.S.keyword";

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
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { DbIndexNames.CASH_DRAWER_ID_INDEX })
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NUMBER)
    private String number;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.PAYMENT_TYPE)
    private String paymentType;

    @DynamoDBAttribute(attributeName = DbAttrNames.TOTAL)
    private BigDecimal total;

    @DynamoDBAttribute(attributeName = DbAttrNames.SUB_TOTAL)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAX_TOTAL)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.DISCOUNT_TOTAL)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAXABLE_SUB_TOTAL)
    private BigDecimal taxableSubTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.NON_TAXABLE_SUB_TOTAL)
    private BigDecimal nonTaxableSubTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBAttribute(attributeName = DbAttrNames.LINE_ITEMS)
    @DynamoDBTypeConverted(converter = LineItemConverter.class)
    private List<LineItem> lineItems = new ArrayList<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_DRAWER_ID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CASH_DRAWER_ID_INDEX)
    private String cashDrawerId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_PAYMENT_AMOUNT)
    private BigDecimal cashPaymentAmount = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREDIT_PAYMENT_AMOUNT)
    private BigDecimal creditPaymentAmount = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.BALANCE_DUE)
    private BigDecimal balanceDue = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.TRANSACTIONS)
    @DynamoDBTypeConverted(converter = TransactionConverter.class)
    private List<Transaction> transactions = new ArrayList<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.PAID_DATE)
    private Date paidDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.HAS_RETURNS)
    private Boolean hasReturns;

    @DynamoDBAttribute(attributeName = DbAttrNames.PAID_OUT_CODE_ITEMS)
    @DynamoDBTypeConverted(converter = PaidOutCodeItemsConverter.class)
    private List<PaidOutCodeItem> paidOutCodeItems = new ArrayList<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.REASON_CODES)
    @DynamoDBTypeConverted(converter = ReasonCodesConverter.class)
    private List<ReasonCodeItem> reasonCodes = new ArrayList<>();

    @DynamoDBAttribute(attributeName = DbAttrNames.LINKED_ORDER_ID)
    private String linkedOrderId;

    @DynamoDBAttribute(attributeName = DbAttrNames.TOTAL_COST)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.PROFIT)
    private BigDecimal profit = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.MARGIN)
    private BigDecimal margin = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.PAID_OUT_TOTAL)
    private BigDecimal paidOutTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.CASH_TRANSACTION_COUNT)
    private int cashTransactionCount;

    @DynamoDBAttribute(attributeName = DbAttrNames.CARD_TRANSACTION_COUNT)
    private int cardTransactionCount;

    @DynamoDBAttribute(attributeName = DbAttrNames.SALES_REP)
    private String salesRep;

    @DynamoDBAttribute(attributeName = DbAttrNames.RETURN_NON_TAXABLE_SUB_TOTAL)
    private BigDecimal returnNonTaxableSubTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.RETURN_TAXABLE_SUB_TOTAL)
    private BigDecimal returnTaxableSubTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.RETURN_TOTAL)
    private BigDecimal returnTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.RETURN_TAX_TOTAL)
    private BigDecimal returnTaxTotal = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_ID)
    private String customerId;

    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_NAME)
    private String customerName;

    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_NUMBER)
    private String customerNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.ACCOUNT_NAME)
    private String accountName;

    @DynamoDBAttribute(attributeName = DbAttrNames.ACCOUNT_NUMBER)
    private String accountNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.IDENTIFIER)
    private String identifier;

    @DynamoDBAttribute(attributeName = DbAttrNames.BUYER)
    private String buyer;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREDIT_LIMIT)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.BALANCE)
    private BigDecimal balance = BigDecimal.ZERO;

    @DynamoDBAttribute(attributeName = DbAttrNames.TYPE)
    private String type;

    @DynamoDBAttribute(attributeName = DbAttrNames.PO_NUMBER)
    private String poNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.TENDERED)
    private BigDecimal tendered;

    @DynamoDBAttribute(attributeName = DbAttrNames.DELIVERED)
    private Boolean delivered;

    @DynamoDBAttribute(attributeName = DbAttrNames.EXPIRATION_DATE)
    private Date expirationDate;

    @DynamoDBAttribute(attributeName = DbAttrNames.DELIVERY_ID)
    private String deliveryId;

    @DynamoDBAttribute(attributeName = DbAttrNames.TAX_ID)
    private String taxId;

    @DynamoDBAttribute(attributeName = DbAttrNames.FAILED_TRANSACTION_COUNT)
    private Integer failedTransactionCount = 0;

    @Override
    public String toString() {
        return "Order [id=" + id + ", number=" + number + ", orderProduct=" + lineItems + "]";
    }
}
