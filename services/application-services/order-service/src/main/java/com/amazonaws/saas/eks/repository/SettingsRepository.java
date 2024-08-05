package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.settings.model.Settings;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SettingsRepository {
    private static final Logger logger = LogManager.getLogger(SettingsRepository.class);

    @Autowired
    private DynamoDBMapper mapper;

    public Settings get(String tenantId) {
        return mapper.load(Settings.class, tenantId);
    }
}
