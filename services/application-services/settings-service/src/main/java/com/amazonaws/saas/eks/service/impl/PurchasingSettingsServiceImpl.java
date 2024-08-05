package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.clients.AddressServiceClient;
import com.amazonaws.saas.eks.exception.InvalidArgumentsException;
import com.amazonaws.saas.eks.repository.PurchasingSettingsRepository;
import com.amazonaws.saas.eks.service.PurchasingSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.CreatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.purchasing.UpdatePurchasingSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.AddressResponse;
import com.amazonaws.saas.eks.settings.dto.responses.PurchasingSettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.PurchasingSettingsMapper;
import com.amazonaws.saas.eks.settings.model.v2.purchasing.PurchasingSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class PurchasingSettingsServiceImpl implements PurchasingSettingsService {

	private static final Logger logger = LogManager.getLogger(PurchasingSettingsServiceImpl.class);

	private final PurchasingSettingsRepository settingsRepository;

	private final AddressServiceClient addressServiceClient;

	public PurchasingSettingsServiceImpl(PurchasingSettingsRepository settingsRepository,
										 AddressServiceClient addressServiceClient) {
		this.settingsRepository = settingsRepository;
		this.addressServiceClient = addressServiceClient;
	}

	@Override
	public PurchasingSettingsResponse create(String tenantId, CreatePurchasingSettingsRequest request) {
		PurchasingSettings settings = PurchasingSettingsMapper.INSTANCE.createPurchasingSettingsRequestToPurchasingSettings(request);

		String county = getCountyFromAddressResponse(tenantId, request.getState(), request.getCity());
		settings.setCounty(county);

		settings.setCreated(new Date());
		settings.setModified(settings.getCreated());

		PurchasingSettings createdPurchasingSettings = settingsRepository.create(tenantId, settings);

		return PurchasingSettingsMapper.INSTANCE.purchasingSettingsToPurchasingSettingsResponse(createdPurchasingSettings);
	}

	@Override
	public PurchasingSettingsResponse get(String tenantId) {
		PurchasingSettings settings = settingsRepository.get(tenantId);
		return PurchasingSettingsMapper.INSTANCE.purchasingSettingsToPurchasingSettingsResponse(settings);
	}

	@Override
	public PurchasingSettingsResponse update(String tenantId, UpdatePurchasingSettingsRequest request) {
		PurchasingSettings newSettings = PurchasingSettingsMapper.INSTANCE.updatePurchasingSettingsRequestToPurchasingSettings(request);

		String county = getCountyFromAddressResponse(tenantId, request.getState(), request.getCity());
		newSettings.setCounty(county);

		PurchasingSettings updatedSettings = settingsRepository.update(tenantId, newSettings);

		return PurchasingSettingsMapper.INSTANCE.purchasingSettingsToPurchasingSettingsResponse(updatedSettings);
	}

	@Override
	public void delete(String tenantId) {
		settingsRepository.delete(tenantId);
	}

	private String getCountyFromAddressResponse(String tenantId, String state, String city) {
		AddressResponse addressResponse = null;
		try {
			addressResponse = addressServiceClient.getAddress(state, city).getBody();
		} catch (Exception e) {
			logger.error("Error getting address for tenant {}. Error: {}", tenantId, e);
		}
		if (addressResponse == null || !StringUtils.hasLength(addressResponse.getCounty())) {
			throw new InvalidArgumentsException(String.format("Invalid address (state=%s, city=%s) provided. County not found. Tenant: %s", state, city, tenantId));
		}
		return addressResponse.getCounty();
	}
}
