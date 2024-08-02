package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.payment.model.Transaction;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private static final Logger logger = LogManager.getLogger(TransactionRepository.class);

    @Autowired
    private DynamoDBMapper mapper;

    public Transaction get(String tenantId, String transactionId) {
        Transaction model = mapper.load(Transaction.class, tenantId, transactionId);
        if (model == null) {
            throw new EntityNotFoundException("Transaction", transactionId, tenantId, "-");
        }
        return model;
    }

    public Transaction insert(String tenantId, Transaction transaction) {
        transaction.setPartitionKey(tenantId);
        transaction.setId(String.valueOf(UUID.randomUUID()));
        mapper.save(transaction);

        return get(tenantId, transaction.getId());
    }

    public List<Transaction> getByOrderNumber(String tenantId, String orderNumber) {
        Map<String, AttributeValue> eav = new HashMap<>();
        Map<String, String> ean = new HashMap<>();
        eav.put(":partitionKey", new AttributeValue().withS(tenantId));
        eav.put(":orderNumber", new AttributeValue().withS(orderNumber));
        DynamoDBQueryExpression<Transaction> query = new DynamoDBQueryExpression<Transaction>()
                .withIndexName(Transaction.INDEX_ORDER_NUMBER)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = :partitionKey AND %s = :orderNumber",
                        Transaction.ATTR_PARTITION_KEY, Transaction.ATTR_ORDER_NUMBER))
                .withExpressionAttributeValues(eav);
        try {
            return mapper.query(Transaction.class, query);
        } catch (Exception e) {
            logger.error("Error reading transactions for orderNumber " + orderNumber, e);
            throw e;
        }
    }
}
