package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.ProductOrder;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOrderRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(ProductOrderRepository.class);

    public ProductOrder get(String productId, String orderId, String tenantId) {
        ProductOrder productOrder;
        String productOrderId = ProductOrder.buildSortKey(productId, orderId);
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            productOrder = mapper.load(ProductOrder.class, EntityType.PRODUCT_ORDERS.getLabel(), productOrderId);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get product-order by ID failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
        if (productOrder == null) {
            throw new EntityNotFoundException(String.format("Product-order not found. ID: %s", productOrderId));
        }
        return productOrder;
    }

    public ProductOrder save(ProductOrder model, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            mapper.save(model);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Save product-order failed %s", tenantId, e.getMessage()));
        }
        return model;
    }

    public void delete(String productId, String orderId, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            ProductOrder ProductOrder = get(productId, orderId, tenantId);
            mapper.delete(ProductOrder);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Delete product-order failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }
}
