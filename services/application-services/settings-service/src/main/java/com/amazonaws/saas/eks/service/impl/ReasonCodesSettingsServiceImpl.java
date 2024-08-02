package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.repository.ReasonCodesSettingsRepository;
import com.amazonaws.saas.eks.service.ReasonCodesSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.CreateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.reasoncodes.UpdateReasonCodesSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.ReasonCodesSettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.ReasonCodesSettingsMapper;
import com.amazonaws.saas.eks.settings.model.v2.reasoncodes.ReasonCodesSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReasonCodesSettingsServiceImpl implements ReasonCodesSettingsService {

	@Autowired
	private ReasonCodesSettingsRepository settingsRepository;

	@Override
	public ReasonCodesSettingsResponse create(String tenantId, CreateReasonCodesSettingsRequest request) {
		ReasonCodesSettings settings = ReasonCodesSettingsMapper.INSTANCE.createReasonCodesSettingsRequestToReasonCodesSettings(request);

		settings.setCreated(new Date());
		settings.setModified(settings.getCreated());

		ReasonCodesSettings createdReasonCodesSettings = settingsRepository.create(tenantId, settings);

		return ReasonCodesSettingsMapper.INSTANCE.reasonCodesSettingsToReasonCodesSettingsResponse(createdReasonCodesSettings);
	}

	@Override
	public ReasonCodesSettingsResponse get(String tenantId) {
		ReasonCodesSettings settings = settingsRepository.get(tenantId);
		return ReasonCodesSettingsMapper.INSTANCE.reasonCodesSettingsToReasonCodesSettingsResponse(settings);
	}

	@Override
	public ReasonCodesSettingsResponse update(String tenantId, UpdateReasonCodesSettingsRequest request) {
		ReasonCodesSettings newSettings = ReasonCodesSettingsMapper.INSTANCE.updateReasonCodesSettingsRequestToReasonCodesSettings(request);

		ReasonCodesSettings updatedSettings = settingsRepository.update(tenantId, newSettings);

		return ReasonCodesSettingsMapper.INSTANCE.reasonCodesSettingsToReasonCodesSettingsResponse(updatedSettings);
	}

	@Override
	public void delete(String tenantId, String code) {
		settingsRepository.delete(tenantId, code);
	}
}
