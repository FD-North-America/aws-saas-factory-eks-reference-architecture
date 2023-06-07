package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.saas.eks.model.CashDrawerCheckout;
import com.amazonaws.saas.eks.model.enums.CashDrawerStatus;
import com.amazonaws.saas.eks.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class CashDrawerCheckoutRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CashDrawerCheckoutRepository.class);

    public CashDrawerCheckout create(CashDrawerCheckout cashDrawerCheckout, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(cashDrawerCheckout.getPartitionKey())) {
                cashDrawerCheckout.setPartitionKey(EntityType.CASHDRAWERCHECKOUTS.getLabel());
            }
            if (!StringUtils.hasLength(cashDrawerCheckout.getId())) {
                cashDrawerCheckout.setId(String.valueOf(UUID.randomUUID()));
            }
            if (cashDrawerCheckout.getCreated() == null) {
                cashDrawerCheckout.setCreated(new Date());
            }
            if (cashDrawerCheckout.getModified() == null) {
                cashDrawerCheckout.setModified(cashDrawerCheckout.getCreated());
            }

            cashDrawerCheckout.setStatus(CashDrawerStatus.CHECKED.toString());
            mapper.save(cashDrawerCheckout);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Create Cash Drawer Checkout failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }

        return cashDrawerCheckout;
    }

    public CashDrawerCheckout getByCashDrawerId(String cashDrawerId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, AttributeValue> eav = new HashMap<>();
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + CashDrawerCheckout.STATUS, CashDrawerCheckout.STATUS); // create alias for reserved word "Status"
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.CASHDRAWERCHECKOUTS.getLabel()));
        eav.put(":cashDrawerId", new AttributeValue().withS(cashDrawerId));
        eav.put(":status", new AttributeValue().withS(CashDrawerStatus.CHECKED.toString()));
        DynamoDBQueryExpression<CashDrawerCheckout> query = new DynamoDBQueryExpression<CashDrawerCheckout>()
                .withIndexName(CashDrawerCheckout.CASH_DRAWER_ID_INDEX)
                .withConsistentRead(false)
                .withFilterExpression(String.format("#%s = :status", CashDrawerCheckout.STATUS))
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :cashDrawerId", CashDrawerCheckout.PARTITION_KEY,
                        CashDrawerCheckout.CASH_DRAWER_ID))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        List<CashDrawerCheckout> results = mapper.query(CashDrawerCheckout.class, query);
        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    public CashDrawerCheckout update(CashDrawerCheckout cashDrawerCheckout, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        cashDrawerCheckout.setModified(new Date());
        mapper.save(cashDrawerCheckout);
        return cashDrawerCheckout;
    }
}
