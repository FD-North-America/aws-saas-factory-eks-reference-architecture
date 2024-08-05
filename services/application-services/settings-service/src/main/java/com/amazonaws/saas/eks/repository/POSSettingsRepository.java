package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.saas.eks.settings.model.enums.SequenceNumberType;
import com.amazonaws.saas.eks.settings.model.v2.pos.POSSettings;
import com.amazonaws.saas.eks.settings.model.v2.pos.SequenceNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public class POSSettingsRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(POSSettingsRepository.class);

	public POSSettings create(String tenantId, POSSettings settings) {
		// ToDo: If pos settings exists, throw exception
		settings.setPartitionKey(POSSettings.buildPartitionKey(tenantId));
		settings.setId(EntityType.POS.getLabel());
		mapper.save(settings);
		return get(tenantId);
	}

	public POSSettings get(String tenantId) {
		POSSettings model = mapper.load(POSSettings.class, POSSettings.buildPartitionKey(tenantId), EntityType.POS.getLabel());
		if (model == null) {
			throw new EntityNotFoundException(String.format("POS settings not found for tenant '%s'", tenantId));
		}
		return model;
	}

	public POSSettings update(String tenantId, POSSettings settings) {
		POSSettings model = get(tenantId);

		if (settings.getSequenceNumbers() != null && !settings.getSequenceNumbers().isEmpty()) {
			model.setSequenceNumbers(settings.getSequenceNumbers());
		}

		if (settings.getDisallowCashReceiptOptions() != null) {
			model.setDisallowCashReceiptOptions(settings.getDisallowCashReceiptOptions());
		}

		if (settings.getPickingTicketPrintOptions() != null) {
			model.setPickingTicketPrintOptions(settings.getPickingTicketPrintOptions());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId) {
		POSSettings model = get(tenantId);
		mapper.delete(model);
	}

	public String getNextSequence(String tenantId, SequenceNumberType type) {
		String nextNumber = null;

		POSSettings model = get(tenantId);
		SequenceNumber sn = null;
		for (SequenceNumber s : model.getSequenceNumbers()) {
			if (s.getType().equals(type.toString())) {
				sn = s;
				s.setNextNumber(s.getNextNumber() + 1);
			}
		}

		if (sn != null) {
			model.setModified(new Date());
			mapper.save(model);
			String format = "%s%0" + sn.getSize() + "d";
			nextNumber = String.format(format, sn.getNumberFormatSetup(), sn.getNextNumber());
		}

		return nextNumber;
	}
}
