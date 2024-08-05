package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.settings.model.enums.SalesTaxJurisdiction;
import com.amazonaws.saas.eks.settings.model.v2.salestax.SalesTaxSettings;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SalesTaxSettingsRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(SalesTaxSettingsRepository.class);

    private static final String PK_PLACEHOLDER = ":partitionKey";

    public SalesTaxSettings insert(String tenantId, String parentSalesTaxSettingsId, SalesTaxSettings salesTaxSettings) {
        SalesTaxSettings parentSalesTaxSettings = null;
        if (StringUtils.hasLength(parentSalesTaxSettingsId)) {
            parentSalesTaxSettings = getBaseSalesTaxSettings(tenantId, parentSalesTaxSettingsId);
        }
        String key = String.valueOf(UUID.randomUUID());
        String path = parentSalesTaxSettings != null
                ? constructNewPath(parentSalesTaxSettings.getSalesTaxPath(), key)
                : key;
        salesTaxSettings.setPartitionKey(SalesTaxSettings.buildPartitionKey(tenantId));
        salesTaxSettings.setSalesTaxPath(path);
        salesTaxSettings.setId(key);
        salesTaxSettings.setJurisdiction(findJurisdiction(path));
        mapper.save(salesTaxSettings);
        return get(tenantId, key);
    }

    public SalesTaxSettings get(String tenantId, String id) {
        SalesTaxSettings salesTaxSettings = getBaseSalesTaxSettings(tenantId, id);
        return salesTaxSettingsWithDescendants(tenantId, salesTaxSettings);
    }

    public List<SalesTaxSettings> getAll(String tenantId,
                                         String filter,
                                         String jurisdiction,
                                         String state,
                                         String city
    ) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PK_PLACEHOLDER, new AttributeValue().withS(SalesTaxSettings.buildPartitionKey(tenantId)));
        DynamoDBQueryExpression<SalesTaxSettings> query = new DynamoDBQueryExpression<SalesTaxSettings>()
                .withKeyConditionExpression(String.format("%s = %s", SalesTaxSettings.DbAttrNames.PARTITION_KEY, PK_PLACEHOLDER))
                .withExpressionAttributeValues(eav);
        PaginatedQueryList<SalesTaxSettings> salesTaxSettingsQueryResult = mapper.query(SalesTaxSettings.class, query);

        if (StringUtils.hasLength(jurisdiction)) {
            return salesTaxSettingsQueryResult.stream()
                    .filter(x -> x.getJurisdiction().equals(jurisdiction)
                            && (!StringUtils.hasLength(filter)
                                || x.getDescription().toLowerCase().contains(filter.toLowerCase())
                                || x.getCode().toLowerCase().contains(filter.toLowerCase()))
                    ).collect(Collectors.toList());
        }

        List<SalesTaxSettings> salesTaxSettingsList = new ArrayList<>(salesTaxSettingsQueryResult);
        salesTaxSettingsList.sort(Comparator.comparing(SalesTaxSettings::getCode));

        if (StringUtils.hasLength(filter)) {
            return salesTaxSettingsListWithDescendants(filterList(salesTaxSettingsList, filter));
        }

        if (StringUtils.hasLength(state) && StringUtils.hasLength(city)) {
            return filterListByStateAndCity(salesTaxSettingsList, state, city);
        }

        return salesTaxSettingsListWithDescendants(salesTaxSettingsList);
    }

    public SalesTaxSettings update(String tenantId, String id, String newParentId, SalesTaxSettings salesTaxSettings) {
        SalesTaxSettings model = getBaseSalesTaxSettings(tenantId, id);
        if (StringUtils.hasLength(salesTaxSettings.getCode())) {
            model.setCode(salesTaxSettings.getCode());
        }
        if (StringUtils.hasLength(salesTaxSettings.getDescription())) {
            model.setDescription(salesTaxSettings.getDescription());
        }
        if (salesTaxSettings.getRate() != null) {
            model.setRate(salesTaxSettings.getRate());
        }
        if (salesTaxSettings.getTaxableLimit() != null) {
            model.setTaxableLimit(salesTaxSettings.getTaxableLimit());
        }
        if (salesTaxSettings.getTaxingState() != null) {
            model.setTaxingState(salesTaxSettings.getTaxingState());
        }
        model.setModified(new Date());

        if (StringUtils.hasLength(newParentId)) {
            SalesTaxSettings newParent = getBaseSalesTaxSettings(tenantId, newParentId);
            String newPath = constructNewPath(newParent.getSalesTaxPath(), model.getId());
            List<Object> objectsToWrite = updateDescendants(tenantId, model, newPath);
            model.setSalesTaxPath(newPath);
            mapper.batchWrite(objectsToWrite, new ArrayList<>());
        }

        mapper.save(model);
        return get(tenantId, model.getId());
    }

    public void delete(SalesTaxSettings model) {
        for (SalesTaxSettings m: model.getSalesTaxes()) {
            delete(m);
        }
        mapper.delete(model);
    }

    private SalesTaxSettings getBaseSalesTaxSettings(String tenantId, String id) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PK_PLACEHOLDER, new AttributeValue().withS(SalesTaxSettings.buildPartitionKey(tenantId)));
        eav.put(":sortKey", new AttributeValue().withS(id));

        DynamoDBQueryExpression<SalesTaxSettings> query = new DynamoDBQueryExpression<SalesTaxSettings>()
                .withKeyConditionExpression("PartitionKey = :partitionKey and SortKey = :sortKey")
                .withExpressionAttributeValues(eav);
        PaginatedQueryList<SalesTaxSettings> results = mapper.query(SalesTaxSettings.class, query);

        if (results.isEmpty()) {
            throw new EntityNotFoundException(String.format("Sales Tax Settings not found. ID: %s. TenantId: %s", id,
                    tenantId));
        }

        return results.get(0);
    }

    private SalesTaxSettings salesTaxSettingsWithDescendants(String tenantId, SalesTaxSettings salesTaxSettings) {
        List<SalesTaxSettings> descendants = getDescendants(tenantId, salesTaxSettings.getSalesTaxPath());
        Map<String, SalesTaxSettings> map = Maps.uniqueIndex(descendants, SalesTaxSettings::getId);
        for (SalesTaxSettings settings: descendants) {
            String[] ids = settings.getSalesTaxPath().split(SalesTaxSettings.KEY_DELIMITER);
            if (ids.length > 1) {
                String parentId = ids[ids.length - 2];
                if (map.containsKey(parentId)) {
                    Objects.requireNonNull(map.get(parentId)).getSalesTaxes().add(settings);
                }
            }
        }
        return map.get(salesTaxSettings.getId());
    }

    private List<SalesTaxSettings> getDescendants(String tenantId, String path) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PK_PLACEHOLDER, new AttributeValue().withS(SalesTaxSettings.buildPartitionKey(tenantId)));
        eav.put(":path", new AttributeValue().withS(path));
        DynamoDBQueryExpression<SalesTaxSettings> query = new DynamoDBQueryExpression<SalesTaxSettings>()
                .withIndexName(SalesTaxSettings.DbIndexNames.SALES_TAX_PATH_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("PartitionKey = :partitionKey and begins_with(SalesTaxPath, :path)")
                .withExpressionAttributeValues(eav);
        return mapper.query(SalesTaxSettings.class, query);
    }

    private String constructNewPath(String currentPath, String id) {
        return String.join(SalesTaxSettings.KEY_DELIMITER, currentPath, id);
    }

    private String findJurisdiction(String path) {
        int keyLength = path.split(SalesTaxSettings.KEY_DELIMITER).length;
        switch (keyLength) {
            case 3:
                return SalesTaxJurisdiction.CITY.toString();
            case 2:
                return SalesTaxJurisdiction.COUNTY.toString();
            default:
                return SalesTaxJurisdiction.STATE.toString();
        }
    }

    private List<SalesTaxSettings> filterList(List<SalesTaxSettings> salesTaxSettingsList, String filter) {
        Set<SalesTaxSettings> filteredSet = new HashSet<>();
        String filterValue = filter.toLowerCase();
        List<SalesTaxSettings> matchResults = salesTaxSettingsList
                .stream()
                .filter(c -> c.getDescription().toLowerCase().contains(filterValue)
                        || c.getCode().toLowerCase().contains(filterValue))
                .collect(Collectors.toList());


        for (SalesTaxSettings c : salesTaxSettingsList) {
            for (SalesTaxSettings match : matchResults) {
                if (match.getId().equals(c.getId()) || match.getSalesTaxPath().contains(c.getId())) {
                    filteredSet.add(c);
                }
            }
        }

        return new ArrayList<>(filteredSet);
    }

    private List<SalesTaxSettings> filterListByStateAndCity(List<SalesTaxSettings> salesTaxSettingsList, String state, String city) {
        List<SalesTaxSettings> result = new ArrayList<>();

        // Get State that matches the state parameter
        Optional<SalesTaxSettings> matchState = salesTaxSettingsList.stream()
                .filter(setting -> setting.getJurisdiction().equals(SalesTaxJurisdiction.STATE.toString())
                        && setting.getCode().equalsIgnoreCase(state))
                .findFirst();

        if (matchState.isEmpty()) {
            return result;
        }

        SalesTaxSettings stateSettings = matchState.get();

        // Get City that matches the city parameter
        Optional<SalesTaxSettings> matchCity = salesTaxSettingsList.stream()
                .filter(setting -> setting.getJurisdiction().equals(SalesTaxJurisdiction.CITY.toString())
                        && setting.getSalesTaxPath().startsWith(stateSettings.getId())
                        && setting.getDescription().toLowerCase().contains(city.toLowerCase()))
                .findFirst();

        if (matchCity.isPresent()) {
            // Get County using countyId from matchCity
            String matchCityId = matchCity.get().getSalesTaxPath().split(SalesTaxSettings.KEY_DELIMITER)[1];

            Optional<SalesTaxSettings> matchCounty = salesTaxSettingsList.stream()
                    .filter(setting -> setting.getJurisdiction().equals(SalesTaxJurisdiction.COUNTY.toString())
                            && setting.getSalesTaxPath().startsWith(stateSettings.getId())
                            && setting.getId().equals(matchCityId))
                            .findFirst();

            if (matchCounty.isPresent()) {
                result.add(stateSettings);
                matchCounty.get().getSalesTaxes().add(matchCity.get());
                stateSettings.getSalesTaxes().add(matchCounty.get());
            }
        }

        return result;
    }

    private List<SalesTaxSettings> salesTaxSettingsListWithDescendants(List<SalesTaxSettings> salesTaxSettingsList) {
        List<SalesTaxSettings> result = new ArrayList<>();
        Map<String, SalesTaxSettings> map = Maps.uniqueIndex(salesTaxSettingsList, SalesTaxSettings::getId);
        for (SalesTaxSettings c: salesTaxSettingsList) {
            String[] cIds = c.getSalesTaxPath().split(SalesTaxSettings.KEY_DELIMITER);
            if (cIds.length == 1) {
                result.add(c);
            }
            else if (cIds.length > 1) {
                String parentId = cIds[cIds.length - 2];
                if (map.containsKey(parentId)) {
                    Objects.requireNonNull(map.get(parentId)).getSalesTaxes().add(c);
                }
            }
        }
        return result;
    }

    private List<Object> updateDescendants(String tenantId, SalesTaxSettings model, String newPath) {
        List<Object> objectsToWrite = new ArrayList<>();
        List<SalesTaxSettings> descendants = getDescendants(tenantId, model.getSalesTaxPath())
                .stream()
                .filter(c -> !c.getId().equals(model.getId()))
                .collect(Collectors.toList());
        for (SalesTaxSettings d : descendants) {
            d.setSalesTaxPath(constructNewPath(newPath, d.getId()));
            d.setModified(model.getModified());
            objectsToWrite.add(d);
        }
        return objectsToWrite;
    }
}
