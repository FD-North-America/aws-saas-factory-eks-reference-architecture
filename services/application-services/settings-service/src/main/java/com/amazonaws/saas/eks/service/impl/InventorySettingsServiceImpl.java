package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.repository.InventorySettingsRepository;
import com.amazonaws.saas.eks.service.InventorySettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.CreateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.inventory.UpdateInventorySettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.InventorySettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.InventorySettingsMapper;
import com.amazonaws.saas.eks.settings.model.v2.inventory.InventorySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InventorySettingsServiceImpl implements InventorySettingsService {

	@Autowired
	private InventorySettingsRepository settingsRepository;

	@Override
	public InventorySettingsResponse create(String tenantId, CreateInventorySettingsRequest request) {
		InventorySettings settings = InventorySettingsMapper.INSTANCE.createInventorySettingsRequestToInventorySettings(request);

		settings.setCreated(new Date());
		settings.setModified(settings.getCreated());

		InventorySettings createdInventorySettings = settingsRepository.create(tenantId, settings);

		return InventorySettingsMapper.INSTANCE.inventorySettingsToInventorySettingsResponse(createdInventorySettings);
	}

	@Override
	public InventorySettingsResponse get(String tenantId) {
		InventorySettings settings = settingsRepository.get(tenantId);
		return InventorySettingsMapper.INSTANCE.inventorySettingsToInventorySettingsResponse(settings);
	}

	@Override
	public InventorySettingsResponse update(String tenantId, UpdateInventorySettingsRequest request) {
		InventorySettings newSettings = InventorySettingsMapper.INSTANCE.updateInventorySettingsRequestToInventorySettings(request);

		InventorySettings updatedSettings = settingsRepository.update(tenantId, newSettings);

		return InventorySettingsMapper.INSTANCE.inventorySettingsToInventorySettingsResponse(updatedSettings);
	}

	@Override
	public void delete(String tenantId) {
		settingsRepository.delete(tenantId);
	}
}
