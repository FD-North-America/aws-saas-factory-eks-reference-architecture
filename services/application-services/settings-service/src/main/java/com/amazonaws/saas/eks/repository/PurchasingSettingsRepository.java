package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.saas.eks.settings.model.v2.pos.POSSettings;
import com.amazonaws.saas.eks.settings.model.v2.purchasing.PurchasingSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;


@Repository
public class PurchasingSettingsRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(PurchasingSettingsRepository.class);

	public PurchasingSettings create(String tenantId, PurchasingSettings settings) {
		settings.setPartitionKey(POSSettings.buildPartitionKey(tenantId));
		settings.setId(EntityType.PURCHASING.getLabel());
		mapper.save(settings);
		return get(tenantId);
	}

	public PurchasingSettings get(String tenantId) {
		PurchasingSettings model = mapper.load(PurchasingSettings.class, PurchasingSettings.buildPartitionKey(tenantId),
				EntityType.PURCHASING.getLabel());
		if (model == null) {
			String message = String.format("Purchasing settings not found for tenant '%s'", tenantId);
			logger.error(message);
			throw new EntityNotFoundException(message);
		}
		return model;
	}

	public PurchasingSettings update(String tenantId, PurchasingSettings settings) {
		PurchasingSettings model = get(tenantId);

		if (StringUtils.hasLength(settings.getCompanyName())) {
			model.setCompanyName(settings.getCompanyName());
		}

		if (StringUtils.hasLength(settings.getBranch())) {
			model.setBranch(settings.getBranch());
		}

		if (StringUtils.hasLength(settings.getAddress())) {
			model.setAddress(settings.getAddress());
		}

		if (settings.getContact() != null) {
			model.setContact(settings.getContact());
		}

		if (StringUtils.hasLength(settings.getCity())) {
			model.setCity(settings.getCity());
		}

		if (StringUtils.hasLength(settings.getPhone())) {
			model.setPhone(settings.getPhone());
		}

		if (StringUtils.hasLength(settings.getState())) {
			model.setState(settings.getState());
		}

		if (StringUtils.hasLength(settings.getCounty())) {
			model.setCounty(settings.getCounty());
		}

		if (StringUtils.hasLength(settings.getZip())) {
			model.setZip(settings.getZip());
		}

		if (settings.getFax() != null) {
			model.setFax(settings.getFax());
		}

		if (StringUtils.hasLength(settings.getOrderNumberFormat())) {
			model.setOrderNumberFormat(settings.getOrderNumberFormat());
		}

		if (settings.getPurchasingOptions() != null) {
			model.setPurchasingOptions(settings.getPurchasingOptions());
		}

		if (settings.getReceivingOptions() != null) {
			model.setReceivingOptions(settings.getReceivingOptions());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId) {
		PurchasingSettings model = get(tenantId);
		mapper.delete(model);
	}
}
