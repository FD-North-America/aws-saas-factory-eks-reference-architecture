package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.saas.eks.settings.model.v2.reasoncodes.ReasonCodesSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;


@Repository
public class ReasonCodesSettingsRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(ReasonCodesSettingsRepository.class);

	public ReasonCodesSettings create(String tenantId, ReasonCodesSettings settings) {
		// ToDo: If pos settings exists, throw exception
		settings.setPartitionKey(ReasonCodesSettings.buildPartitionKey(tenantId));
		settings.setId(EntityType.REASON_CODES.getLabel());
		mapper.save(settings);
		return get(tenantId);
	}

	public ReasonCodesSettings get(String tenantId) {
		ReasonCodesSettings model = mapper.load(ReasonCodesSettings.class,
				ReasonCodesSettings.buildPartitionKey(tenantId), EntityType.REASON_CODES.getLabel());
		if (model == null) {
			throw new EntityNotFoundException(String.format("Reason Codes settings not found for tenant '%s'", tenantId));
		}
		return model;
	}

	public ReasonCodesSettings update(String tenantId, ReasonCodesSettings settings) {
		ReasonCodesSettings model = get(tenantId);

		if (settings.getReasonCodes() != null) {
			model.setReasonCodes(settings.getReasonCodes());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId, String code) {
		ReasonCodesSettings model = get(tenantId);
		if (StringUtils.hasLength(code)) {
			model.getReasonCodes().removeIf(x -> x.getCode().equals(code));
			mapper.save(model);
		} else {
			mapper.delete(model);
		}
	}
}
