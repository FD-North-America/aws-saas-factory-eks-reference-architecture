package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.dto.responses.settings.SettingsResponse;
import com.amazonaws.saas.eks.mapper.SettingsMapper;
import com.amazonaws.saas.eks.model.Settings;
import com.amazonaws.saas.eks.repository.SettingsRepository;
import com.amazonaws.saas.eks.service.SettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {
    private static final Logger logger = LogManager.getLogger(SettingsServiceImpl.class);

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public SettingsResponse get(String tenantId) {
        Settings s = settingsRepository.get(tenantId);
        return SettingsMapper.INSTANCE.settingsToSettingsResponse(s);
    }
}
