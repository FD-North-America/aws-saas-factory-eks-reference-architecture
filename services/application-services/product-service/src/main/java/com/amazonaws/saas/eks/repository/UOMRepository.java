package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.UOMByBarcodeNotFoundException;
import com.amazonaws.saas.eks.exception.UOMNotFoundException;
import com.amazonaws.saas.eks.model.EntityType;
import com.amazonaws.saas.eks.model.UOM;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class UOMRepository {
    private static final Logger logger = LogManager.getLogger(UOMRepository.class);

    // TODO: MOVE TO CONFIG OR INPUT
    public static final String STORE_ID = "store1";

    @Autowired
    private DynamoDBMapper mapper;

    public UOM insert(String tenantId, UOM uom) {
        String uomId = String.valueOf(UUID.randomUUID());
        uom.setPartitionKey(getPartitionKey(tenantId));
        uom.setId(uomId);
        uom.setCreated(new Date());
        uom.setModified(uom.getCreated());
        mapper.save(uom);
        return get(tenantId, uomId);
    }

    public UOM get(String tenantId, String uomId) {
        UOM uom = mapper.load(UOM.class, getPartitionKey(tenantId), uomId);
        if (uom == null) {
            throw new UOMNotFoundException(uomId, tenantId, STORE_ID);
        }

        return uom;
    }

    public List<UOM> getUOMbyProductId(String tenantId, String productId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
        eav.put(":productId", new AttributeValue().withS(productId));
        DynamoDBQueryExpression<UOM> query = new DynamoDBQueryExpression<UOM>()
                .withIndexName(UOM.PRODUCT_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :productId", UOM.PARTITION_KEY,
                        UOM.PRODUCT_ID))
                .withExpressionAttributeValues(eav);
        return mapper.query(UOM.class, query);
    }

    public UOM getByBarcode(String tenantId, String barcode) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
        eav.put(":barcode", new AttributeValue().withS(barcode));

        DynamoDBQueryExpression<UOM> query = new DynamoDBQueryExpression<UOM>()
                .withIndexName(UOM.BARCODE_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(UOM.PARTITION_KEY + " = :partitionKey AND " + UOM.BARCODE + " = :barcode")
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<UOM> uoms = mapper.query(UOM.class, query);
        if (uoms.isEmpty()) {
            throw new UOMByBarcodeNotFoundException(barcode, tenantId, STORE_ID);
        }
        return uoms.get(0);
    }

    public UOM update(String tenantId, String uomId, UOM uom) {
        UOM model = get(tenantId, uomId);
        if (!StringUtils.isEmpty(uom.getName())) {
            model.setName(uom.getName());
        }
        if (uom.getFactor() != null && uom.getFactor() > 0) {
            model.setFactor(uom.getFactor());
        }
        if (!StringUtils.isEmpty(uom.getBarcode())) {
            model.setBarcode(uom.getBarcode());
        }
        if (!StringUtils.isEmpty(uom.getAlternateId())) {
            model.setAlternateId(uom.getAlternateId());
        }
        model.setModified(new Date());
        mapper.save(model);
        return get(tenantId, model.getId());
    }

    public void delete(String tenantId, String uomId) {
        UOM model = get(tenantId, uomId);
        mapper.delete(model);
    }

    private String getPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, UOM.KEY_DELIMITER, STORE_ID, UOM.KEY_DELIMITER,
                EntityType.UOM.getLabel());
    }
}
