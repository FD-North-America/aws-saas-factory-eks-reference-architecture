package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.ChargeCode;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Repository
public class ChargeCodeRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(ChargeCodeRepository.class);

    public ChargeCode get(String tenantId, String chargeCodeId) {
        ChargeCode chargeCode;
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            chargeCode = mapper.load(ChargeCode.class, EntityType.CHARGE_CODES.getLabel(), chargeCodeId);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get charge-code by ID failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
        if (chargeCode == null) {
            throw new EntityNotFoundException(String.format("Charge code not found. ID: %s", chargeCodeId));
        }
        return chargeCode;
    }

    public ChargeCode save(String tenantId, ChargeCode model) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(model.getPartitionKey())) {
                model.setPartitionKey(EntityType.CHARGE_CODES.getLabel());
            }
            if (!StringUtils.hasLength(model.getId())) {
                model.setId(String.valueOf(UUID.randomUUID()));
            }
            if (model.getCreated() == null) {
                model.setCreated(new Date());
            }
            if (model.getModified() == null) {
                model.setModified(model.getCreated());
            } else {
                model.setModified(new Date());
            }
            mapper.save(model);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Save charge-code failed %s", tenantId, e.getMessage()));
        }
        return model;
    }

    public void delete(String tenantId, String chargeCodeId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            ChargeCode chargeCode = get(tenantId, chargeCodeId);
            mapper.delete(chargeCode);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Delete charge-code failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }
}
