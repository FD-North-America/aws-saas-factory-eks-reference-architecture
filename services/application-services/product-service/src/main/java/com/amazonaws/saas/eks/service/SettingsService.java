package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.responses.settings.SettingsResponse;

public interface SettingsService {
    SettingsResponse get(String tenantId);
}
