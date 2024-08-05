package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.Delivery;
import com.amazonaws.saas.eks.order.model.enums.DeliveryStatus;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Repository
public class DeliveryRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(DiscountRepository.class);

    public Delivery save(String tenantId, Delivery delivery) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        if (!StringUtils.hasLength(delivery.getPartitionKey())) {
            delivery.setPartitionKey(EntityType.DELIVERIES.getLabel());
        }
        if (!StringUtils.hasLength(delivery.getId())) {
            delivery.setId(String.valueOf(UUID.randomUUID()));
        }
        if (!StringUtils.hasLength(delivery.getStatus())) {
            delivery.setStatus(DeliveryStatus.ACTIVE.toString());
        }
        if (delivery.getCreated() == null) {
            delivery.setCreated(new Date());
        }
        if (delivery.getModified() == null) {
            delivery.setModified(delivery.getCreated());
        } else {
            delivery.setModified(new Date());
        }
        try {
            mapper.save(delivery);
        } catch (Exception e) {
            logger.error("TenantId: {}-Save delivery failed {}", tenantId, e.getMessage());
        }

        return delivery;
    }

    public Delivery get(String tenantId, String deliveryId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Delivery delivery = null;

        try {
            delivery = mapper.load(Delivery.class, EntityType.DELIVERIES.getLabel(), deliveryId);
        } catch (Exception e) {
            logger.error("TenantId: {}-Get Delivery By ID failed {}", tenantId, e.getMessage());
        }
        return delivery;
    }

    public void delete(String tenantId, String deliveryId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        try {
            Delivery delivery = get(tenantId, deliveryId);
            if (delivery == null) {
                throw new EntityNotFoundException(EntityType.DELIVERIES.getLabel());
            }
            delivery.setStatus(DeliveryStatus.DELETED.toString());
            delivery.setModified(new Date());
            mapper.save(delivery);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Delete Delivery failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }
}
