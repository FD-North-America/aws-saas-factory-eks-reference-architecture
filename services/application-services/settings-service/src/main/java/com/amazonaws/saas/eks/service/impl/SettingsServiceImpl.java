package com.amazonaws.saas.eks.service.impl;


import com.amazonaws.saas.eks.service.SettingsService;
import org.springframework.stereotype.Service;

import com.amazonaws.saas.eks.repository.SettingsRepository;

@Service
public class SettingsServiceImpl implements SettingsService {
	private final SettingsRepository settingsRepository;

	public SettingsServiceImpl(SettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}
}
