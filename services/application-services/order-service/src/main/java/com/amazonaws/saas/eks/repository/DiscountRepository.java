package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.order.model.Discount;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Repository
public class DiscountRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(DiscountRepository.class);

    public Discount save(Discount discount, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(discount.getPartitionKey())) {
                discount.setPartitionKey(EntityType.DISCOUNTS.getLabel());
            }
            if (!StringUtils.hasLength(discount.getId())) {
                discount.setId(String.valueOf(UUID.randomUUID()));
            }
            if (discount.getCreated() == null) {
                discount.setCreated(new Date());
            }
            if (discount.getModified() == null) {
                discount.setModified(discount.getCreated());
            } else {
                discount.setModified(new Date());
            }
            mapper.save(discount);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Save discount failed %s", tenantId, e.getMessage()));
        }

        return discount;
    }

    public Discount get(String discountId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Discount discount = null;

        try {
            discount = mapper.load(Discount.class, EntityType.DISCOUNTS.getLabel(), discountId);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Get Discount By ID failed %s", tenantId, e.getMessage()));
        }
        return discount;
    }

    public void delete(String discountId, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            Discount discount = get(discountId, tenantId);
            mapper.delete(discount);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Delete Order failed %s", tenantId, e.getMessage()));
        }
    }
}
