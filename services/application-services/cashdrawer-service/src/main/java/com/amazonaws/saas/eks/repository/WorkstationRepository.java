package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.cashdrawer.model.Workstation;
import com.amazonaws.saas.eks.cashdrawer.model.enums.EntityType;
import com.amazonaws.saas.eks.exception.CashDrawerException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class WorkstationRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(WorkstationRepository.class);

    public List<Workstation> getAll(String tenantId) {
        List<Workstation> workstations;
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":partitionKey", new AttributeValue().withS(EntityType.WORKSTATIONS.getLabel()));
            DynamoDBQueryExpression<Workstation> query = new DynamoDBQueryExpression<Workstation>()
                    .withKeyConditionExpression(String.format("%s = :partitionKey", Workstation.DbAttrNames.PARTITION_KEY))
                    .withExpressionAttributeValues(eav);
            workstations = mapper.query(Workstation.class, query);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get all workstations failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }
        return workstations;
    }

    public Optional<Workstation> get(String tenantId, String workstationId) {
        Workstation workstation;
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            workstation = mapper.load(Workstation.class, EntityType.WORKSTATIONS.getLabel(), workstationId);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Get workstation by ID failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CashDrawerException(message);
        }
        return Optional.of(workstation);
    }

    public Workstation save(String tenantId, Workstation model) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (model.getPartitionKey() == null) {
                model.setPartitionKey(EntityType.WORKSTATIONS.getLabel());
            }
            if (!StringUtils.hasLength(model.getId())) {
                model.setId(String.valueOf(UUID.randomUUID()));
            }
            if (!StringUtils.hasLength(model.getNumber())) {
                model.setNumber(String.valueOf(getLatestCounter(tenantId, EntityType.WORKSTATIONS)));
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
            logger.error(String.format("TenantId: %s-Save workstation failed %s", tenantId, e.getMessage()));
        }
        return model;
    }

    public void delete(String tenantId, String workstationId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            Workstation workstation = new Workstation();
            workstation.setPartitionKey(EntityType.WORKSTATIONS.getLabel());
            workstation.setId(workstationId);
            mapper.delete(workstation);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s-Delete workstation failed %s", tenantId, e.getMessage()));
        }
    }
}
