package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.SearchException;
import com.amazonaws.saas.eks.exception.UOMByBarcodeNotFoundException;
import com.amazonaws.saas.eks.exception.UOMNotFoundException;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.UOM;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Repository
public class UOMRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(UOMRepository.class);
    private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
    private static final String PRODUCT_ID_PLACEHOLDER = ":productId";
    private static final String BARCODE_PLACEHOLDER = ":barcode";

    public UOM insert(String tenantId, UOM uom) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        String uomId = String.valueOf(UUID.randomUUID());
        uom.setPartitionKey(UOM.buildPartitionKey(tenantId));
        uom.setId(uomId);
        uom.setCreated(new Date());
        uom.setModified(uom.getCreated());
        mapper.save(uom);
        return get(tenantId, uomId);
    }

    public UOM get(String tenantId, String uomId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        UOM uom = mapper.load(UOM.class, UOM.buildPartitionKey(tenantId), uomId);
        if (uom == null) {
            throw new UOMNotFoundException(uomId, tenantId, Product.STORE_ID);
        }

        return uom;
    }

    public List<UOM> getByProductId(String tenantId, String productId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(UOM.buildPartitionKey(tenantId)));
        eav.put(PRODUCT_ID_PLACEHOLDER, new AttributeValue().withS(productId));
        DynamoDBQueryExpression<UOM> query = new DynamoDBQueryExpression<UOM>()
                .withIndexName(UOM.DbIndexNames.PRODUCT_ID_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s AND %s = %s",
                        UOM.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        UOM.DbAttrNames.PRODUCT_ID, PRODUCT_ID_PLACEHOLDER))
                .withExpressionAttributeValues(eav);
        return mapper.query(UOM.class, query);
    }

    public UOM getByBarcode(String tenantId, String barcode) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(UOM.buildPartitionKey(tenantId)));
        eav.put(BARCODE_PLACEHOLDER, new AttributeValue().withS(barcode));

        DynamoDBQueryExpression<UOM> query = new DynamoDBQueryExpression<UOM>()
                .withIndexName(UOM.DbIndexNames.BARCODE_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s AND %s = %s",
                        UOM.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        UOM.DbAttrNames.BARCODE, BARCODE_PLACEHOLDER))
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<UOM> uoms = mapper.query(UOM.class, query);
        if (uoms.isEmpty()) {
            throw new UOMByBarcodeNotFoundException(barcode, tenantId, Product.STORE_ID);
        }
        return uoms.get(0);
    }

    public UOM update(String tenantId, String uomId, UOM uom) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        UOM model = get(tenantId, uomId);
        if (StringUtils.hasLength(uom.getName())) {
            model.setName(uom.getName());
        }
        if (uom.getFactor() != null && uom.getFactor() > 0) {
            model.setFactor(uom.getFactor());
        }
        if (StringUtils.hasLength(uom.getBarcode())) {
            model.setBarcode(uom.getBarcode());
        }
        if (StringUtils.hasLength(uom.getAlternateId())) {
            model.setAlternateId(uom.getAlternateId());
        }
        model.setModified(new Date());
        mapper.save(model);
        return get(tenantId, model.getId());
    }

    public void delete(String tenantId, String uomId) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        UOM model = get(tenantId, uomId);
        mapper.delete(model);
    }

    public List<UOM> searchByIdentifier(String tenantId, String identifier) {
        List<UOM> uomList;
        List<Query> mustQueries = new ArrayList<>();
        MatchQuery pKeyQuery = QueryBuilders.match()
                .field(UOM.OpenSearch.FieldNames.PARTITION_KEY)
                .query(FieldValue.of(UOM.buildPartitionKey(tenantId)))
                .build();
        mustQueries.add(pKeyQuery._toQuery());

        List<Query> shouldQueries = new ArrayList<>();
        MatchQuery barcodeQuery = QueryBuilders.match()
                .field(UOM.OpenSearch.FieldNames.BARCODE)
                .query(FieldValue.of(identifier))
                .build();
        MatchQuery alternateIdQuery = QueryBuilders.match()
                .field(UOM.OpenSearch.FieldNames.ALTERNATE_ID)
                .query(FieldValue.of(identifier))
                .build();
        shouldQueries.add(barcodeQuery._toQuery());
        shouldQueries.add(alternateIdQuery._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(mustQueries)
                .should(shouldQueries)
                .minimumShouldMatch("1")
                .build();

        SearchRequest req =  SearchRequest.of(s -> s
                .index(UOM.OpenSearch.getIndex(tenantId))
                .query(boolQuery._toQuery()));

        try {
            SearchResponse<JsonNode> results = openSearchClient.search(req, JsonNode.class);
            uomList = convertSearchResultsToModels(results, UOM.class, tenantId);
        } catch (IOException e) {
            logger.error("Error reading from OpenSearch: ", e);
            throw new SearchException("Error reading from OpenSearch");
        }

        return uomList;
    }
}
