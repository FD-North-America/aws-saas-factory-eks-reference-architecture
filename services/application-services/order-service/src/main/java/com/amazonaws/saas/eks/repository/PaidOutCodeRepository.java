package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.PaidOutCode;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

@Repository
public class PaidOutCodeRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(PaidOutCodeRepository.class);

    private static final String CODE_PREFIX = "PdOut";

    public PaidOutCode save(PaidOutCode paidOutCode, String tenantId) {
        try {
            if (!StringUtils.hasLength(paidOutCode.getPartitionKey())) {
                paidOutCode.setPartitionKey(EntityType.PAIDOUTCODES.getLabel());
            }
            if (!StringUtils.hasLength(paidOutCode.getId())) {
                paidOutCode.setId(String.valueOf(UUID.randomUUID()));
            }
            if (paidOutCode.getCreated() == null) {
                paidOutCode.setCreated(new Date());
            }
            if (paidOutCode.getModified() == null) {
                paidOutCode.setModified(paidOutCode.getCreated());
            } else {
                paidOutCode.setModified(new Date());
            }
            if (!StringUtils.hasLength(paidOutCode.getCode())) {
                paidOutCode.setCode(String.format("%s%s", CODE_PREFIX, Instant.now().getEpochSecond()));
            }

            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            mapper.save(paidOutCode);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Save paid out code failed %s", tenantId, e.getMessage()));
        }

        return paidOutCode;
    }

    public void batchUpdate(List<PaidOutCode> paidOutCodes, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        List<Object> objectsToWrite = new ArrayList<>(paidOutCodes);
        mapper.batchWrite(objectsToWrite, new ArrayList<>());
    }

    public List<PaidOutCode> getByCode(String code, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.PAIDOUTCODES.getLabel()));
        eav.put(":code", new AttributeValue().withS(code));
        DynamoDBQueryExpression<PaidOutCode> query = new DynamoDBQueryExpression<PaidOutCode>()
                .withIndexName(PaidOutCode.DbIndexNames.CODE_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :code",
                        PaidOutCode.DbAttrNames.PARTITION_KEY, PaidOutCode.DbAttrNames.CODE))
                .withExpressionAttributeValues(eav);
        try {
            return mapper.query(PaidOutCode.class, query);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get paid out code by its code failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }
}
