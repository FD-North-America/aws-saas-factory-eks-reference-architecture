package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.EntityType;
import com.amazonaws.saas.eks.settings.model.v2.accountsreceivable.AccountsReceivableSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public class AccountsReceivableSettingsRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(AccountsReceivableSettingsRepository.class);

	public AccountsReceivableSettings create(String tenantId, AccountsReceivableSettings settings) {
		// ToDo: If accounts receivable settings exists, throw exception
		settings.setPartitionKey(AccountsReceivableSettings.buildPartitionKey(tenantId));
		settings.setId(EntityType.ACCOUNTS_RECEIVABLE.getLabel());
		mapper.save(settings);
		return get(tenantId);
	}

	public AccountsReceivableSettings get(String tenantId) {
		AccountsReceivableSettings model = mapper.load(AccountsReceivableSettings.class,
				AccountsReceivableSettings.buildPartitionKey(tenantId), EntityType.ACCOUNTS_RECEIVABLE.getLabel());
		if (model == null) {
			throw new EntityNotFoundException(String.format("AccountsReceivable settings not found for tenant '%s'", tenantId));
		}
		return model;
	}

	public AccountsReceivableSettings update(String tenantId, AccountsReceivableSettings settings) {
		AccountsReceivableSettings model = get(tenantId);

		if (settings.getCustomerTypes() != null) {
			model.setCustomerTypes(settings.getCustomerTypes());
		}

		model.setModified(new Date());

		mapper.save(model);

		return model;
	}

	public void delete(String tenantId) {
		AccountsReceivableSettings model = get(tenantId);
		mapper.delete(model);
	}
}
