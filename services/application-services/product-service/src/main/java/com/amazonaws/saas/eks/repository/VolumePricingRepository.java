package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.VolumePricingNotFoundException;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.saas.eks.product.model.VolumePricing;
import com.amazonaws.saas.eks.product.model.enums.VolumePricingMode;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Repository
public class VolumePricingRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(VolumePricingRepository.class);
    private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
    private static final String PRODUCT_ID_PLACEHOLDER = ":productId";
    private static final String UOM_ID_PLACEHOLDER = ":uomId";

    public VolumePricing insert(String tenantId, VolumePricing volumePricing) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        volumePricing.setPartitionKey(VolumePricing.buildPartitionKey(tenantId));
        mapper.save(volumePricing);

        return get(tenantId, volumePricing.getId());
    }

    public VolumePricing get(String tenantId, String volumePricingId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        VolumePricing model = mapper.load(VolumePricing.class, VolumePricing.buildPartitionKey(tenantId), volumePricingId);
        if (model == null) {
            throw new VolumePricingNotFoundException(volumePricingId, tenantId, Product.STORE_ID);
        }
        return model;
    }

    public List<VolumePricing> getByProductId(String tenantId, String productId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(VolumePricing.buildPartitionKey(tenantId)));
        eav.put(PRODUCT_ID_PLACEHOLDER, new AttributeValue().withS(productId));

        DynamoDBQueryExpression<VolumePricing> query = new DynamoDBQueryExpression<VolumePricing>()
                .withIndexName(VolumePricing.DbIndexNames.PRODUCT_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s AND %s = %s",
                        VolumePricing.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        VolumePricing.DbAttrNames.PRODUCT_ID, PRODUCT_ID_PLACEHOLDER))
                .withExpressionAttributeValues(eav);

        return mapper.query(VolumePricing.class, query);
    }

    public VolumePricing update(String tenantId, VolumePricing volumePricing) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        volumePricing.setModified(new Date());
        mapper.save(volumePricing);
        return volumePricing;
    }

    public void delete(String tenantId, String id) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        VolumePricing model = get(tenantId, id);
        mapper.delete(model);
    }

    public void updateOnProductRetailPriceChange(String tenantId, Product product) {
        List<VolumePricing> vpList = getByProductId(tenantId, product.getId());
        for (VolumePricing vp : vpList) {
            BigDecimal price = computePrice(vp.getMode(), vp.getFactor(), product.getRetailPrice(), vp.getDiscount());
            vp.setPrice(price);
        }
        batchUpdate(tenantId, vpList);
    }

    public void updateOnUOMFactorChange(String tenantId, Product product, UOM uom) {
        List<VolumePricing> vpList = getAllByUomId(tenantId, uom.getId());
        for (VolumePricing vp : vpList) {
            Double factor = computeFactor(uom.getFactor(), vp.getBreakPointQty());
            vp.setFactor(factor);
            BigDecimal price = computePrice(vp.getMode(), vp.getFactor(), product.getRetailPrice(), vp.getDiscount());
            vp.setPrice(price);
        }
        batchUpdate(tenantId, vpList);
    }

    public Double computeFactor(Double uomFactor, Integer breakPointQty) {
        return uomFactor * breakPointQty;
    }

    public BigDecimal computePrice(String mode, Double factor, BigDecimal productRetailPrice, BigDecimal discount) {
        BigDecimal price = BigDecimal.ZERO;
        if (mode.equals(VolumePricingMode.FLAT_RATE.toString())) {
            price = productRetailPrice.subtract(discount);
        } else if (mode.equals(VolumePricingMode.DISCOUNT_PERCENTAGE.toString())) {
            price = BigDecimal.valueOf(factor)
                    .multiply(productRetailPrice)
                    .multiply(discount)
                    .divide(new BigDecimal(100), RoundingMode.HALF_UP);
        }
        return price.setScale(2, RoundingMode.UP);
    }

    private void batchUpdate(String tenantId, List<VolumePricing> modelsToUpdate) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        mapper.batchWrite(modelsToUpdate, new ArrayList<>());
    }

    private List<VolumePricing> getAllByUomId(String tenantId, String uomId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(VolumePricing.buildPartitionKey(tenantId)));
        eav.put(UOM_ID_PLACEHOLDER, new AttributeValue().withS(uomId));

        DynamoDBQueryExpression<VolumePricing> query = new DynamoDBQueryExpression<VolumePricing>()
                .withIndexName(VolumePricing.DbIndexNames.UOM_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s AND %s = %s",
                        VolumePricing.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        VolumePricing.DbAttrNames.UOM_ID, UOM_ID_PLACEHOLDER))
                .withExpressionAttributeValues(eav);

        return mapper.query(VolumePricing.class, query);
    }
}
