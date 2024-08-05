package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.customer.model.Certificate;
import com.amazonaws.saas.eks.customer.model.enums.EntityType;
import com.amazonaws.saas.eks.exception.CustomerException;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CertificateRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CertificateRepository.class);

    private static final String PK_PLACEHOLDER = ":partitionKey";
    private static final String CUSTOMER_ID_PLACEHOLDER = ":customerId";

    public Certificate create(Certificate certificate, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (!StringUtils.hasLength(certificate.getPartitionKey())) {
                certificate.setPartitionKey(EntityType.CERTIFICATES.getLabel());
            }
            if (!StringUtils.hasLength(certificate.getId())) {
                certificate.setId(String.valueOf(UUID.randomUUID()));
            }
            if (certificate.getCreated() == null) {
                certificate.setCreated(new Date());
            }
            if (certificate.getModified() == null) {
                certificate.setModified(certificate.getCreated());
            }
            mapper.save(certificate);
        } catch (Exception e) {
            String message = String.format("TenantId: %s - Save Certificate failed with error %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CustomerException(message);
        }
        return certificate;
    }

    public Certificate get(String certificateId, String tenantId) {
        Certificate model = null;
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            model = mapper.load(Certificate.class, EntityType.CERTIFICATES.getLabel(), certificateId);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s - Get certificate by ID failed with error: %s", tenantId, e.getMessage()));
        }
        if (model == null) {
            throw new EntityNotFoundException(String.format("Certificate not found. ID: %s", certificateId));
        }
        return model;
    }

    public List<Certificate> getAllByCustomerId(String customerId, String tenantId, String filter) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PK_PLACEHOLDER, new AttributeValue().withS(Certificate.buildPartitionKey(tenantId)));
        eav.put(CUSTOMER_ID_PLACEHOLDER, new AttributeValue().withS(customerId));
        DynamoDBQueryExpression<Certificate> query = new DynamoDBQueryExpression<Certificate>()
                .withIndexName(Certificate.DbIndexNames.CUSTOMER_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s AND %s = %s",
                        Certificate.DbAttrNames.PARTITION_KEY, PK_PLACEHOLDER,
                        Certificate.DbAttrNames.CUSTOMER_ID, CUSTOMER_ID_PLACEHOLDER))
                .withExpressionAttributeValues(eav);
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            PaginatedQueryList<Certificate> certificatesQueryResult = mapper.query(Certificate.class, query);
            List<Certificate> certificatesList = new ArrayList<>(certificatesQueryResult);
            certificatesList.sort(Comparator.comparing(Certificate::getExpiryDate));
            return certificatesQueryResult.stream()
                    .filter(x -> !StringUtils.hasLength(filter)
                            || x.getName().toLowerCase().contains(filter.toLowerCase())
                            || x.getNumber().toLowerCase().contains(filter.toLowerCase())
                    ).collect(Collectors.toList());
        } catch (Exception e) {
            String message = String.format("TenantId: %s - Get all certificates by customer failed with error %s", tenantId, e.getMessage());
            logger.error(message);
            throw new CustomerException(message);
        }
    }

    public Certificate update(String certificateId, String tenantId, Certificate certificate) {
        Certificate model = get(certificateId, tenantId);
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            if (StringUtils.hasLength(certificate.getNumber())) {
                model.setNumber(certificate.getNumber());
            }
            if (StringUtils.hasLength(certificate.getName())) {
                model.setName(certificate.getName());
            }
            if (certificate.getExpiryDate() != null) {
                model.setExpiryDate(certificate.getExpiryDate());
            }
            model.setModified(new Date());
            mapper.save(model);
        } catch (Exception e) {
            String message = String.format("TenantId: %s - Update certificate failed with error %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
        return model;
    }

    public void delete(String certificateId, String tenantId) {
        try {
            DynamoDBMapper mapper = dynamoDBMapper(tenantId);
            Certificate certificate = get(certificateId, tenantId);
            mapper.delete(certificate);
        } catch (Exception e) {
            logger.error(String.format("TenantId: %s - Delete certificate failed with error: %s", tenantId, e.getMessage()));
        }
    }
}
