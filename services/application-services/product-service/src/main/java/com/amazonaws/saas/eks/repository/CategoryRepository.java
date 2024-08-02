package com.amazonaws.saas.eks.repository;

import com.amazonaws.saas.eks.exception.ProductCategoryNotFoundException;
import com.amazonaws.saas.eks.product.model.Category;
import com.amazonaws.saas.eks.product.model.Product;
import com.amazonaws.saas.eks.product.model.enums.CategoryLevel;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CategoryRepository extends BaseRepository {
    private static final Logger logger = LogManager.getLogger(CategoryRepository.class);
    private static final String PARTITION_KEY_PLACEHOLDER = ":partitionKey";
    private static final String CATEGORY_PATH_PLACEHOLDER = ":path";
    private static final String SORT_KEY_PLACEHOLDER = ":sortKey";

    public Category insert(String tenantId, String parentCategoryId, Category category) {
        Category parentCategory = null;
        if (StringUtils.hasLength(parentCategoryId)) {
            try {
                parentCategory = getBaseCategory(tenantId, parentCategoryId);
            } catch (ProductCategoryNotFoundException e) {
                throw new ProductCategoryNotFoundException(parentCategoryId, CategoryLevel.CATEGORY.getLabel(),
                        Product.STORE_ID);
            }
        }
        String key = String.valueOf(UUID.randomUUID());
        String path = parentCategory != null ? constructNewPath(parentCategory.getCategoryPath(), key) : key;
        category.setPartitionKey(Category.buildPartitionKey(tenantId));
        category.setCategoryPath(path);
        category.setId(key);
        category.setLevel(findCategoryLevel(path));
        logger.debug("Inserting category: {}", category.getId());
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        mapper.save(category);
        return get(tenantId, key);
    }

    public List<Category> getAll(String tenantId, String filter, String level) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Category.buildPartitionKey(tenantId)));
        DynamoDBQueryExpression<Category> query = new DynamoDBQueryExpression<Category>()
                .withKeyConditionExpression(String.format("%s = %s", Category.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER))
                .withExpressionAttributeValues(eav);

        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        PaginatedQueryList<Category> categories = mapper.query(Category.class, query);

        if (StringUtils.hasLength(level)) {
            return categories.stream()
                    .filter(c -> c.getLevel().equals(level)
                            && (!StringUtils.hasLength(filter)
                            || c.getName().toLowerCase().contains(filter.toLowerCase())
                            || c.getCode().toLowerCase().contains(filter.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Converting PaginatedQueryList to List to be able to sort by Category Code
        List<Category> categoryList = new ArrayList<>(categories);
        categoryList.sort(Comparator.comparing(Category::getCode));
        if (StringUtils.hasLength(filter)) {
            return categoriesWithDescendants(filterList(categoryList, filter));
        }

        return categoriesWithDescendants(categoryList);
    }

    public Category get(String tenantId, String id) {
        Category category = getBaseCategory(tenantId, id);
        return categoryWithDescendants(tenantId, category);
    }

    public Category update(String tenantId, String id, String newParentId, Category category) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Category model = getBaseCategory(tenantId, id);
        model.setName(category.getName());
        model.setDescription(category.getDescription());
        model.setCode(category.getCode());
        model.setModified(new Date());

        if (StringUtils.hasLength(newParentId)) {
            Category newParent = getBaseCategory(tenantId, newParentId);
            String newPath = constructNewPath(newParent.getCategoryPath(), model.getId());
            List<Object> objectsToWrite = updateDescendants(tenantId, model, newPath);
            model.setCategoryPath(newPath);
            mapper.batchWrite(objectsToWrite, new ArrayList<>());
        }

        mapper.save(model);
        return get(tenantId, model.getId());
    }

    public void delete(String tenantId, String id) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);
        Category model = get(tenantId, id);
        mapper.delete(model);
    }

    public Category getBaseCategory(String tenantId, String id) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Category.buildPartitionKey(tenantId)));
        eav.put(SORT_KEY_PLACEHOLDER, new AttributeValue().withS(id));

        DynamoDBQueryExpression<Category> query = new DynamoDBQueryExpression<Category>()
                .withKeyConditionExpression(String.format("%s = %s and %s = %s",
                        Category.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        Category.DbAttrNames.SORT_KEY, SORT_KEY_PLACEHOLDER))
                .withExpressionAttributeValues(eav);
        PaginatedQueryList<Category> results = mapper.query(Category.class, query);

        if (results.isEmpty()) {
            throw new ProductCategoryNotFoundException(id, Product.STORE_ID);
        }

        return results.get(0);
    }

    private List<Category> categoriesWithDescendants(List<Category> categories) {
        List<Category> result = new ArrayList<>();

        Map<String, Category> categoryMap = Maps.uniqueIndex(categories, Category::getId);
        for (Category c: categories) {
            String[] cIds = c.getCategoryPath().split(Product.KEY_DELIMITER);
            if (cIds.length == 1) {
                result.add(c);
            }
            else if (cIds.length > 1) {
                String parentId = cIds[cIds.length - 2];
                if (categoryMap.containsKey(parentId)) {
                    categoryMap.get(parentId).getCategories().add(c);
                }
            }
        }

        return result;
    }

    private List<Category> filterList(List<Category> categories, String filter) {
        Set<Category> filteredSet = new HashSet<>();
        String filterValue = filter.toLowerCase();
        List<Category> matchResults = categories
                .stream()
                .filter(c -> c.getName().toLowerCase().contains(filterValue)
                        || c.getCode().toLowerCase().contains(filterValue))
                .collect(Collectors.toList());
        for (Category c : categories) {
            for (Category match : matchResults) {
                if (match.getId().equals(c.getId()) || match.getCategoryPath().contains(c.getId())) {
                    filteredSet.add(c);
                }
            }
        }

        return new ArrayList<>(filteredSet);
    }

    private Category categoryWithDescendants(String tenantId, Category category) {
        List<Category> descendants = getDescendants(tenantId, category.getCategoryPath());
        Map<String, Category> categoryMap = Maps.uniqueIndex(descendants, Category::getId);
        for (Category c: descendants) {
            String[] cIds = c.getCategoryPath().split(Product.KEY_DELIMITER);
            if (cIds.length > 1) {
                String parentId = cIds[cIds.length - 2];
                if (categoryMap.containsKey(parentId)) {
                    categoryMap.get(parentId).getCategories().add(c);
                }
            }
        }
        return categoryMap.get(category.getId());
    }

    private List<Category> getDescendants(String tenantId, String path) {
        DynamoDBMapper mapper = dynamoDBMapper(tenantId);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(PARTITION_KEY_PLACEHOLDER, new AttributeValue().withS(Category.buildPartitionKey(tenantId)));
        eav.put(CATEGORY_PATH_PLACEHOLDER, new AttributeValue().withS(path));
        DynamoDBQueryExpression<Category> query = new DynamoDBQueryExpression<Category>()
                .withIndexName(Category.DbIndexNames.CATEGORY_PATH_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression(String.format("%s = %s and begins_with(%s, %s)",
                        Category.DbAttrNames.PARTITION_KEY, PARTITION_KEY_PLACEHOLDER,
                        Category.DbAttrNames.PATH, CATEGORY_PATH_PLACEHOLDER))
                .withExpressionAttributeValues(eav);
        return mapper.query(Category.class, query);
    }

    private List<Object> updateDescendants(String tenantId, Category model, String newPath) {
        List<Object> objectsToWrite = new ArrayList<>();
        List<Category> descendants = getDescendants(tenantId, model.getCategoryPath())
                .stream()
                .filter(c -> !c.getId().equals(model.getId()))
                .collect(Collectors.toList());
        for (Category d : descendants) {
            d.setCategoryPath(constructNewPath(newPath, d.getId()));
            d.setModified(model.getModified());
            objectsToWrite.add(d);
        }
        return objectsToWrite;
    }

    private String findCategoryLevel(String path) {
        int keyLength = path.split(Product.KEY_DELIMITER).length;
        switch (keyLength) {
            case 3:
                return CategoryLevel.GROUP.getLabel();
            case 2:
                return CategoryLevel.SUB_CATEGORY.getLabel();
            default:
                return CategoryLevel.CATEGORY.getLabel();
        }
    }

    private String constructNewPath(String currentPath, String categoryId) {
        return String.join(Product.KEY_DELIMITER, currentPath, categoryId);
    }
}
