package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.settings.model.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class SettingsRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(SettingsRepository.class);

    public Settings get(String tenantId) {
        return mapper.load(Settings.class, tenantId);
    }
}
