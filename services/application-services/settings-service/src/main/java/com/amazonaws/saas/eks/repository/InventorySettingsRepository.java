package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.saas.eks.settings.model.v2.inventory.InventorySettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public class InventorySettingsRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(InventorySettingsRepository.class);

	public InventorySettings create(String tenantId, InventorySettings settings) {
		// ToDo: If inventory settings exists, throw exception
		settings.setPartitionKey(InventorySettings.buildPartitionKey(tenantId));
		settings.setId(EntityType.INVENTORY.getLabel());
		mapper.save(settings);
		return get(tenantId);
	}

	public InventorySettings get(String tenantId) {
		InventorySettings model = mapper.load(InventorySettings.class, InventorySettings.buildPartitionKey(tenantId),
				EntityType.INVENTORY.getLabel());
		if (model == null) {
			throw new EntityNotFoundException(String.format("Inventory settings not found for tenant '%s'", tenantId));
		}
		return model;
	}

	public InventorySettings update(String tenantId, InventorySettings settings) {
		InventorySettings model = get(tenantId);

		if (settings.getOrderNumberSequence() != null) {
			model.setOrderNumberSequence(settings.getOrderNumberSequence());
		}

		if (settings.getUnitsOfMeasure() != null && !settings.getUnitsOfMeasure().isEmpty()) {
			model.setUnitsOfMeasure(settings.getUnitsOfMeasure());
		}

		if (settings.getStoreLocationCodes() != null && !settings.getStoreLocationCodes().isEmpty()) {
			model.setStoreLocationCodes(settings.getStoreLocationCodes());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId) {
		InventorySettings model = get(tenantId);
		mapper.delete(model);
	}
}
