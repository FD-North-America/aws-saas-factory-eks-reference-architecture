package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.SalesHistory;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SalesHistoryRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(SalesHistoryRepository.class);
    private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
    private static final String PRODUCT_ID_PLACEHOLDER = ":productId";

    public List<SalesHistory> get(String tenantId, String productId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(SalesHistory.buildPartitionKey(tenantId)));
        eav.put(PRODUCT_ID_PLACEHOLDER, new AttributeValue().withS(productId));
        DynamoDBQueryExpression<SalesHistory> query = new DynamoDBQueryExpression<SalesHistory>()
                .withKeyConditionExpression(String.format("%s = %s AND begins_with(%s, %s)",
                        SalesHistory.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        SalesHistory.DbAttrNames.SORT_KEY, PRODUCT_ID_PLACEHOLDER))
                .withExpressionAttributeValues(eav);

        return mapper.query(SalesHistory.class, query);
    }

    public List<SalesHistory> batchLoad(String tenantId, List<String> productIds) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        List<KeyPair> keyPairs = new ArrayList<>();
        for (String id : productIds) {
            KeyPair pair = new KeyPair();
            pair.setHashKey(SalesHistory.buildPartitionKey(tenantId));
            pair.setRangeKey(SalesHistory.buildSortKey(id));
            keyPairs.add(pair);
        }
        Map<Class<?>, List<KeyPair>> tableKeyPair = new HashMap<>();
        tableKeyPair.put(SalesHistory.class, keyPairs);

        Map<String, List<Object>> batchResults = mapper.batchLoad(tableKeyPair);
        if (batchResults.isEmpty()) {
            return new ArrayList<>();
        }
        return (List<SalesHistory>) (List<?>) batchResults.get(buildTableName(tenantId));
    }

    public void batchUpdate(String tenantId, List<SalesHistory> salesHistories) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        List<Object> objectsToWrite = new ArrayList<>(salesHistories);
        mapper.batchWrite(objectsToWrite, new ArrayList<>());
    }
}
