package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.VendorNotFoundException;
import com.amazonaws.saas.eks.model.EntityType;
import com.amazonaws.saas.eks.model.Vendor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class VendorRepository {
    private static final Logger logger = LogManager.getLogger(VendorRepository.class);

    // TODO: MOVE TO CONFIG OR INPUT
    public static final String STORE_ID = "store1";

    @Autowired
    private DynamoDBMapper mapper;

    public Vendor insert(String tenantId, Vendor vendor) {
        String vendorId = String.valueOf(UUID.randomUUID());
        vendor.setPartitionKey(getPartitionKey(tenantId));
        vendor.setId(vendorId);
        mapper.save(vendor);
        return get(tenantId, vendorId);
    }

    public Vendor get(String tenantId, String vendorId) {
        Vendor model = mapper.load(Vendor.class, getPartitionKey(tenantId), vendorId);
        if (model == null) {
            throw new VendorNotFoundException(vendorId, STORE_ID);
        }
        return model;
    }

    public List<Vendor> getAll(String tenantId, String filter) {
        Map<String, AttributeValue> eav = new HashMap<>();
        Map<String, String> ean = new HashMap<>();
        ean.put("#" + Vendor.NAME, Vendor.NAME); // create alias to reserved word "Name"
        eav.put(":partitionKey", new AttributeValue().withS(getPartitionKey(tenantId)));
        eav.put(":filter", new AttributeValue().withS(!StringUtils.isEmpty(filter) ? filter : ""));
        DynamoDBQueryExpression<Vendor> query = new DynamoDBQueryExpression<Vendor>()
                .withKeyConditionExpression("PartitionKey = :partitionKey")
                .withFilterExpression(String.format("contains(#%s, :filter)", Vendor.NAME))
                .withExpressionAttributeValues(eav)
                .withExpressionAttributeNames(ean);
        return mapper.query(Vendor.class, query);
    }

    public Vendor update(String tenantId, String vendorId, Vendor vendor) {
        Vendor model = get(tenantId, vendorId);
        model.setName(vendor.getName());
        model.setDescription(vendor.getDescription());
        model.setModified(new Date());
        mapper.save(model);
        return get(tenantId, model.getId());
    }

    public void delete(Vendor model) {
        mapper.delete(model);
    }

    private String getPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, Vendor.KEY_DELIMITER, STORE_ID, Vendor.KEY_DELIMITER,
                EntityType.VENDORS.getLabel());
    }
}
