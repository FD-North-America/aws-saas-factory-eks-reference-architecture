package com.amazonaws.saas.eks.service.impl;

import com.amazonaws.saas.eks.repository.AccountsReceivableSettingsRepository;
import com.amazonaws.saas.eks.service.AccountsReceivableSettingsService;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.CreateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.requests.accountsreceivable.UpdateAccountsReceivableSettingsRequest;
import com.amazonaws.saas.eks.settings.dto.responses.AccountsReceivableSettingsResponse;
import com.amazonaws.saas.eks.settings.mapper.AccountsReceivableSettingsMapper;
import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.AccountsReceivableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountsReceivableSettingsServiceImpl implements AccountsReceivableSettingsService {

	@Autowired
	private AccountsReceivableSettingsRepository settingsRepository;

	@Override
	public AccountsReceivableSettingsResponse create(String tenantId, CreateAccountsReceivableSettingsRequest request) {
		AccountsReceivableSettings settings = AccountsReceivableSettingsMapper.INSTANCE.createARSettingsRequestToARSettings(request);

		settings.setCreated(new Date());
		settings.setModified(settings.getCreated());

		AccountsReceivableSettings createdAccountsReceivableSettings = settingsRepository.create(tenantId, settings);

		return AccountsReceivableSettingsMapper.INSTANCE.arSettingsToARSettingsResponse(createdAccountsReceivableSettings);
	}

	@Override
	public AccountsReceivableSettingsResponse get(String tenantId) {
		AccountsReceivableSettings settings = settingsRepository.get(tenantId);
		return AccountsReceivableSettingsMapper.INSTANCE.arSettingsToARSettingsResponse(settings);
	}

	@Override
	public AccountsReceivableSettingsResponse update(String tenantId, UpdateAccountsReceivableSettingsRequest request) {
		AccountsReceivableSettings newSettings = AccountsReceivableSettingsMapper.INSTANCE.updateARSettingsRequestToARSettings(request);

		AccountsReceivableSettings updatedSettings = settingsRepository.update(tenantId, newSettings);

		return AccountsReceivableSettingsMapper.INSTANCE.arSettingsToARSettingsResponse(updatedSettings);
	}

	@Override
	public void delete(String tenantId) {
		settingsRepository.delete(tenantId);
	}
}
