package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.VolumePricingNotFoundException;
import com.amazonaws.saas.eks.model.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Repository
public class VolumePricingRepository {
    private static final Logger logger = LogManager.getLogger(VolumePricingRepository.class);

    // TODO: MOVE TO CONFIG OR INPUT
    private static final String STORE_ID = "store1";

    @Autowired
    private DynamoDBMapper mapper;

    public VolumePricing insert(String tenantId, VolumePricing volumePricing) {
        volumePricing.setPartitionKey(getPartitionKey(tenantId));
        mapper.save(volumePricing);

        return get(tenantId, volumePricing.getId());
    }

    public VolumePricing get(String tenantId, String volumePricingId) {
        VolumePricing model = mapper.load(VolumePricing.class, getPartitionKey(tenantId), volumePricingId);
        if (model == null) {
            throw new VolumePricingNotFoundException(volumePricingId, tenantId, STORE_ID);
        }
        return model;
    }

    public List<VolumePricing> getAll(String tenantId, String productId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
        eav.put(":productId", new AttributeValue().withS(productId));

        DynamoDBQueryExpression<VolumePricing> query = new DynamoDBQueryExpression<VolumePricing>()
                .withIndexName(VolumePricing.PRODUCT_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :productId",
                        VolumePricing.PARTITION_KEY, VolumePricing.PRODUCT_ID))
                .withExpressionAttributeValues(eav);

        return mapper.query(VolumePricing.class, query);
    }

    public VolumePricing update(String tenantId, VolumePricing volumePricing) {
        volumePricing.setModified(new Date());
        mapper.save(volumePricing);
        return volumePricing;
    }

    public void delete(String tenantId, String id) {
        VolumePricing model = get(tenantId, id);
        mapper.delete(model);
    }

    public void updateOnProductRetailPriceChange(String tenantId, Product product) {
        List<VolumePricing> vpList = getAll(tenantId, product.getId());
        for (VolumePricing vp : vpList) {
            BigDecimal price = computePrice(vp.getMode(), vp.getFactor(), product.getRetailPrice(), vp.getDiscount());
            vp.setPrice(price);
        }
        batchUpdate(vpList);
    }

    public void updateOnUOMFactorChange(String tenantId, Product product, UOM uom) {
        List<VolumePricing> vpList = getAllByUomId(tenantId, uom.getId());
        for (VolumePricing vp : vpList) {
            Double factor = computeFactor(uom.getFactor(), vp.getBreakPointQty());
            vp.setFactor(factor);
            BigDecimal price = computePrice(vp.getMode(), vp.getFactor(), product.getRetailPrice(), vp.getDiscount());
            vp.setPrice(price);
        }
        batchUpdate(vpList);
    }

    public Double computeFactor(Double uomFactor, Integer breakPointQty) {
        return uomFactor * breakPointQty;
    }

    public BigDecimal computePrice(String mode, Double factor, BigDecimal productRetailPrice, BigDecimal discount) {
        BigDecimal price = BigDecimal.ZERO;
        if (mode.equals(VolumePricingMode.FLAT_RATE.toString())) {
            price = BigDecimal.valueOf(factor).multiply(productRetailPrice.subtract(discount));
        } else if (mode.equals(VolumePricingMode.DISCOUNT_PERCENTAGE.toString())) {
            price = BigDecimal.valueOf(factor)
                    .multiply(productRetailPrice)
                    .multiply(discount)
                    .divide(new BigDecimal(100), RoundingMode.HALF_UP);
        }
        return price.setScale(2, RoundingMode.UP);
    }

    private String getPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, VolumePricing.KEY_DELIMITER, STORE_ID, VolumePricing.KEY_DELIMITER,
                EntityType.VOLUME_PRICING.getLabel());
    }

    private void batchUpdate(List<VolumePricing> modelsToUpdate) {
        mapper.batchWrite(modelsToUpdate, new ArrayList<>());
    }

    private List<VolumePricing> getAllByUomId(String tenantId, String uomId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
        eav.put(":uomId", new AttributeValue().withS(uomId));

        DynamoDBQueryExpression<VolumePricing> query = new DynamoDBQueryExpression<VolumePricing>()
                .withIndexName(VolumePricing.UOM_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :uomId",
                        VolumePricing.PARTITION_KEY, VolumePricing.UOM_ID))
                .withExpressionAttributeValues(eav);

        return mapper.query(VolumePricing.class, query);
    }
}
