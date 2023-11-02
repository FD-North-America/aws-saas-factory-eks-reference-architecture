package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.customer.model.Customer;
import com.amazonaws.saas.eks.customer.model.enums.CustomerStatus;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.exception.CustomerException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Repository
public class CustomerRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(CustomerRepository.class);

	/**
	 * Method to save a Customer for a tenant
	 * @param customer {@link Customer to save}
	 * @param tenantId Tenant ID
	 * @return the saved {@link Customer model}
	 */
	public Customer create(Customer customer, String tenantId) {
		try {
			DynamoDBMapper mapper = dynamoDBMapper(tenantId);
			if (!StringUtils.hasLength(customer.getPartitionKey())) {
				customer.setPartitionKey(EntityType.CUSTOMERS.getLabel());
			}
			if (!StringUtils.hasLength(customer.getId())) {
				customer.setId(String.valueOf(UUID.randomUUID()));
			}
			if (customer.getCreated() == null) {
				customer.setCreated(new Date());
			}
			if (customer.getModified() == null) {
				customer.setModified(customer.getCreated());
			}
			customer.setStatus(CustomerStatus.ACTIVE.toString());
			mapper.save(customer);
		} catch (Exception e) {
			String message = String.format("TenantId: %s-Save Customer failed %s", tenantId, e.getMessage());
			logger.error(message);
			throw new CustomerException(message);
		}

		return customer;
	}
}
