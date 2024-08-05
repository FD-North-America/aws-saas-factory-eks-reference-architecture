package com.amazonaws.saas.eks.order.model;

import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.saas.eks.order.model.enums.LineItemType;
import com.amazonaws.saas.eks.order.model.enums.ProductOrderStatus;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@DynamoDBTable(tableName = ProductOrder.TABLE_NAME)
public class ProductOrder {
    public static final String TABLE_NAME = "Order";
    public static final String KEY_DELIMITER = "#";
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String CUSTOMER_NAME = "CustomerName";
        public static final String VENDOR_NAME = "VendorName";
        public static final String ORDER_NUMBER = "OrderNumber";
        public static final String QUANTITY = "Quantity";
        public static final String STATUS = "Status";
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

    @DynamoDBAttribute(attributeName = DbAttrNames.CUSTOMER_NAME)
    private String customerName;

    @DynamoDBAttribute(attributeName = DbAttrNames.VENDOR_NAME)
    private String vendorName;

    @DynamoDBAttribute(attributeName = DbAttrNames.ORDER_NUMBER)
    private String orderNumber;

    @DynamoDBAttribute(attributeName = DbAttrNames.QUANTITY)
    private Integer quantity;

    @DynamoDBAttribute(attributeName = DbAttrNames.STATUS)
    private String status;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    public static String buildSortKey(String productId, String orderId) {
        return String.format("%s%s%s", productId, KEY_DELIMITER, orderId);
    }

    public static List<ProductOrder> buildProductFromOrders(Order order) {
        return order.getLineItems().stream()
            .filter(l -> !l.getType().equals(LineItemType.DISCOUNT.toString()))
            .map(lineItem -> {
                ProductOrder productOrderRequest = new ProductOrder();

                productOrderRequest.setPartitionKey(EntityType.PRODUCT_ORDERS.getLabel());
                productOrderRequest.setId(ProductOrder.buildSortKey(lineItem.getId(), order.getId()));
                productOrderRequest.setOrderNumber(order.getNumber());
                productOrderRequest.setCustomerName(order.getCustomerName());
                productOrderRequest.setQuantity(lineItem.getQuantity());
                productOrderRequest.setCreated(new Date());
                productOrderRequest.setModified(new Date());
                productOrderRequest.setStatus(ProductOrderStatus.COMMITTED.toString());

                return productOrderRequest;
            })
            .collect(Collectors.toList());
    }
}
