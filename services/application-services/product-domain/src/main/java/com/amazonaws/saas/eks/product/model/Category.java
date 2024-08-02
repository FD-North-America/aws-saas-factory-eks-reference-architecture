package com.amazonaws.saas.eks.product.model;

import com.amazonaws.saas.eks.product.model.enums.CategoryLevel;
import com.amazonaws.saas.eks.product.model.enums.EntityType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.amazonaws.saas.eks.product.model.Product.*;

@Setter
@Getter
@DynamoDBTable(tableName = TABLE_NAME)
public class Category {
    public static class DbAttrNames {
        public static final String PARTITION_KEY = "PartitionKey";
        public static final String SORT_KEY = "SortKey";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String CODE = "Code";
        public static final String PATH = "CategoryPath";
        public static final String LEVEL = "Level";
        public static final String CREATED = "Created";
        public static final String MODIFIED = "Modified";

        private DbAttrNames() {
            throw new IllegalStateException();
        }
    }

    public static class DbIndexNames {
        public static final String CATEGORY_PATH_INDEX = "CategoryPath-index";

        private DbIndexNames() {
            throw new IllegalStateException();
        }
    }

    public Category() {
        this.categories = new ArrayList<>();
    }

    @DynamoDBHashKey(attributeName = DbAttrNames.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = DbIndexNames.CATEGORY_PATH_INDEX)
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = DbAttrNames.SORT_KEY)
    private String id;

    @DynamoDBAttribute(attributeName = DbAttrNames.NAME)
    private String name;

    @DynamoDBAttribute(attributeName = DbAttrNames.DESCRIPTION)
    private String description;

    @DynamoDBAttribute(attributeName = DbAttrNames.CODE)
    private String code;

    @DynamoDBAttribute(attributeName = DbAttrNames.PATH)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = DbIndexNames.CATEGORY_PATH_INDEX)
    private String categoryPath;

    @DynamoDBAttribute(attributeName = DbAttrNames.LEVEL)
    private String level;

    @DynamoDBAttribute(attributeName = DbAttrNames.CREATED)
    private Date created;

    @DynamoDBAttribute(attributeName = DbAttrNames.MODIFIED)
    private Date modified;

    @DynamoDBIgnore
    private List<Category> categories;

    @DynamoDBIgnore
    public String getMainCategoryId() {
        if (level.equals(CategoryLevel.SUB_CATEGORY.getLabel())
                || level.equals(CategoryLevel.GROUP.getLabel())) {
            String[] cIds = categoryPath.split(KEY_DELIMITER);
            return cIds.length > 0 ? cIds[0] : null;
        }
        return null;
    }

    @DynamoDBIgnore
    public String getSubCategoryId() {
        if (level.equals(CategoryLevel.GROUP.getLabel())) {
            String[] cIds = categoryPath.split(KEY_DELIMITER);
            return cIds.length > 1 ? cIds[1] : null;
        }
        return null;
    }

    public static String buildPartitionKey(String tenantId) {
        return String.format("%s%s%s%s%s", tenantId, KEY_DELIMITER, STORE_ID, KEY_DELIMITER,
                EntityType.CATEGORIES.getLabel());
    }
}
