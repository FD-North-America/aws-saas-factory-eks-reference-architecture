package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.DeliveryAddressLine;
import com.amazonaws.saas.eks.order.model.Tax;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class TaxRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(TaxRepository.class);

    public Tax save(Tax tax, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(tax.getPartitionKey())) {
                tax.setPartitionKey(EntityType.TAXES.getLabel());
            }
            if (!StringUtils.hasLength(tax.getId())) {
                tax.setId(String.valueOf(UUID.randomUUID()));
            }
            if (tax.getCreated() == null) {
                tax.setCreated(new Date());
            }
            if (tax.getModified() == null) {
                tax.setModified(tax.getCreated());
            } else {
                tax.setModified(new Date());
            }
            mapper.save(tax);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s - Save tax failed with error: %s", tenantId, e.getMessage()));
        }
        return tax;
    }

    public Tax get(String taxId, String tenantId) {
        Tax model = null;
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            model = mapper.load(Tax.class, EntityType.TAXES.getLabel(), taxId);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s - Get tax by ID failed with error: %s", tenantId, e.getMessage()));
        }
        if (model == null) {
            throw new EntityNotFoundException(String.format("Tax not found. ID: %s", taxId));
        }
        return model;
    }

    public Tax getByOrderId(String orderId, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(EntityType.TAXES.getLabel()));
        eav.put(":orderId", new AttributeValue().withS(orderId));
        DynamoDBQueryExpression<Tax> query = new DynamoDBQueryExpression<Tax>()
                .withIndexName(Tax.DbIndexNames.ORDER_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :orderId",
                        Tax.DbAttrNames.PARTITION_KEY, Tax.DbAttrNames.ORDER_ID))
                .withExpressionAttributeValues(eav);

        try {
            PaginatedQueryList<Tax> result = mapper.query(Tax.class, query);
            return (result == null || result.isEmpty()) ? null : result.get(0);
        } catch (Exception e) {
            String message = String.format("TenantId: %s - Get tax by its order failed with error %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }

    public Tax update(String taxId, String tenantId, Tax tax) {
        Tax model = get(taxId, tenantId);
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (StringUtils.hasLength(tax.getType())) {
                model.setType(tax.getType());
            }
            if (StringUtils.hasLength(tax.getExemptCode())) {
                model.setExemptCode(tax.getExemptCode());
            }
            if (StringUtils.hasLength(tax.getCertificateId())) {
                model.setCertificateId(tax.getCertificateId());
            }
            if (tax.getStreetAddress() != null
                    && StringUtils.hasLength(tax.getStreetAddress().getAddress())
                    && !tax.getStreetAddress().getAddress().equalsIgnoreCase(model.getStreetAddress().getAddress())) {
                model.setStreetAddress(tax.getStreetAddress());
            }
            if (StringUtils.hasLength(tax.getZip())) {
                model.setZip(tax.getZip());
            }

            updateTaxDeliveryAddressLine(tax.getState(), model.getState());
            updateTaxDeliveryAddressLine(tax.getCounty(), model.getCounty());
            updateTaxDeliveryAddressLine(tax.getCity(), model.getCity());

            model.setModified(new Date());
            mapper.save(model);
        } catch (Exception e) {
            String message = String.format("TenantId: %s - Update tax failed with error %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
        return model;
    }

    private static void updateTaxDeliveryAddressLine(DeliveryAddressLine source, DeliveryAddressLine target) {
        if (source != null && StringUtils.hasLength(source.getAddress())) {
            if (!source.getAddress().equalsIgnoreCase(target.getAddress())) {
                target.setAddress(source.getAddress());
            }
            if (!Objects.equals(source.getTaxCode(), target.getTaxCode())) {
                target.setTaxCode(source.getTaxCode());
            }
            if (!Objects.equals(source.getTax(), target.getTax())) {
                target.setTax(source.getTax());
            }
        }
    }


    public void delete(String taxId, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            Tax tax = get(taxId, tenantId);
            mapper.delete(tax);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s - Delete tax failed with error: %s", tenantId, e.getMessage()));
        }
    }
}
