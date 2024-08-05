package com.amazonaws.saas.eks.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BaseRepository {
    @Autowired
    protected DynamoDBMapper mapper;
}
