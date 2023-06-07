/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amazonaws.saas.eks.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.saas.eks.model.converters.LineItemConverter;
import com.amazonaws.saas.eks.model.converters.TransactionConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

@DynamoDBTable(tableName = Order.TABLE_NAME)
public class Order {
	public static final String TABLE_NAME = "Order";
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

	public static final String CASH_DRAWER_ID_INDEX = "CashDrawerId-index"; // PK: PartitionKey, SK: CashDrawerId

	@Getter
	@Setter
	@DynamoDBHashKey(attributeName = Order.PARTITION_KEY)
	@DynamoDBIndexHashKey(globalSecondaryIndexNames = {CASH_DRAWER_ID_INDEX})
	private String partitionKey;

	@Getter
	@Setter
	@DynamoDBRangeKey(attributeName = Order.SORT_KEY)
	private String id;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.NUMBER)
	private String number;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.STATUS)
	private String status;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.PAYMENT_TYPE)
	private String paymentType;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.TOTAL)
	private BigDecimal total;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.SUB_TOTAL)
	private BigDecimal subTotal;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.TAX_TOTAL)
	private BigDecimal taxTotal;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.DISCOUNT_TOTAL)
	private BigDecimal discountTotal;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.TAXABLE_SUB_TOTAL)
	private BigDecimal taxableSubTotal;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.NON_TAXABLE_SUB_TOTAL)
	private BigDecimal nonTaxableSubTotal;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.CREATED)
	private Date created;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.MODIFIED)
	private Date modified;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.LINE_ITEMS)
	@DynamoDBTypeConverted(converter = LineItemConverter.class)
	private List<LineItem> lineItems = new ArrayList<>();

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.CASH_DRAWER_ID)
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = CASH_DRAWER_ID_INDEX)
	private String cashDrawerId;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.CASH_PAYMENT_AMOUNT)
	private BigDecimal cashPaymentAmount = BigDecimal.ZERO;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.CREDIT_PAYMENT_AMOUNT)
	private BigDecimal creditPaymentAmount = BigDecimal.ZERO;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.BALANCE_DUE)
	private BigDecimal balanceDue = BigDecimal.ZERO;

	@Getter
	@Setter
	@DynamoDBAttribute(attributeName = Order.TRANSACTIONS)
	@DynamoDBTypeConverted(converter = TransactionConverter.class)
	private List<Transaction> transactions;

	@Override
	public String toString() {
		return "Order [id=" + id + ", number=" + number + ", orderProduct=" + lineItems + "]";
	}

}