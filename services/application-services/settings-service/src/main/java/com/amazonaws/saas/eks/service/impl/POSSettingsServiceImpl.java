package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.service.POSSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.pos.CreatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.pos.UpdatePOSSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.POSSettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.POSSettingsMapper;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;
import com.amazonaws.saas.eks.settings.model.v2.pos.POSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.repository.POSSettingsRepository;

import java.util.Date;

@Service
public class POSSettingsServiceImpl implements POSSettingsService {

	@Autowired
	private POSSettingsRepository settingsRepository;

	@Override
	public POSSettingsResponse create(String tenantId, CreatePOSSettingsRequest request) {
		POSSettings settings = POSSettingsMapper.INSTANCE.createPOSSettingsRequestToPOSSettings(request);

		settings.setCreated(new Date());
		settings.setModified(settings.getCreated());

		POSSettings createdPOSSettings = settingsRepository.create(tenantId, settings);

		return POSSettingsMapper.INSTANCE.posSettingsToPOSSettingsResponse(createdPOSSettings);
	}

	@Override
	public POSSettingsResponse get(String tenantId) {
		POSSettings settings = settingsRepository.get(tenantId);
		return POSSettingsMapper.INSTANCE.posSettingsToPOSSettingsResponse(settings);
	}

	@Override
	public POSSettingsResponse update(String tenantId, UpdatePOSSettingsRequest request) {
		POSSettings newSettings = POSSettingsMapper.INSTANCE.updatePOSSettingsRequestToPOSSettings(request);

		POSSettings updatedSettings = settingsRepository.update(tenantId, newSettings);

		return POSSettingsMapper.INSTANCE.posSettingsToPOSSettingsResponse(updatedSettings);
	}

	@Override
	public void delete(String tenantId) {
		settingsRepository.delete(tenantId);
	}

	@Override
	public String getNextSequence(String tenantId, SequenceNumberType type) {
		return settingsRepository.getNextSequence(tenantId, type);
	}
}
