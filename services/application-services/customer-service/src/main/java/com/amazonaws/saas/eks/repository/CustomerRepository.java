package com.amazonaws.saas.eks.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository extends BaseRepository {
	private static final Logger logger = LogManager.getLogger(CustomerRepository.class);
}
