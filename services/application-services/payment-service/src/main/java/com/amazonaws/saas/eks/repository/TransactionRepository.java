package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.model.Transaction;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
