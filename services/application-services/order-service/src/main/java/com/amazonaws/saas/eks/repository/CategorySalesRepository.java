package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.OrderException;
import com.amazonaws.saas.eks.order.model.CategorySale;
import com.amazonaws.saas.eks.order.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class CategorySalesRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CategorySalesRepository.class);

    public CategorySale get(String tenantId, String categoryId, Date date) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        try {
            return mapper.load(CategorySale.class, EntityType.CATEGORY_SALES.getLabel(), CategorySale.buildSortKey(categoryId, date));
        } catch (Exception e) {
            String message = String.format("TenantId: %s_Get Category Sale failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }
    }

    public CategorySale save(CategorySale sale, String tenantId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        sale.setPartitionKey(EntityType.CATEGORY_SALES.getLabel());
        sale.setSortKey(CategorySale.buildSortKey(sale.getCategoryId(), sale.getCreated()));
        try {
            mapper.save(sale);
        } catch (Exception e) {
            String message = String.format("TenantId: %s-Save Category Sale failed %s", tenantId, e.getMessage());
            logger.error(message);
            throw new OrderException(message);
        }

        return sale;
    }
}
