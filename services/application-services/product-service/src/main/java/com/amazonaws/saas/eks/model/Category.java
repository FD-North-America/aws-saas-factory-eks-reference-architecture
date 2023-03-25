package com.amazonaws.saas.eks.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = Category.TABLE_NAME)
public class Category {
    public static final String TABLE_NAME = "Product_v4";
    public static final String KEY_DELIMITER = "#";
    public static final String PARTITION_KEY = "PartitionKey";
    public static final String SORT_KEY = "SortKey";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String CODE = "Code";
    public static final String PATH = "CategoryPath";
    public static final String LEVEL = "Level";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    public static final String CATEGORY_PATH_INDEX = "CategoryPath-index";

    public Category() {
        this.categories = new ArrayList<>();
    }

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Category.PARTITION_KEY)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Category.CATEGORY_PATH_INDEX)
    private String partitionKey;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = Category.SORT_KEY)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.NAME)
    private String name;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.DESCRIPTION)
    private String description;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.CODE)
    private String code;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.PATH)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Category.CATEGORY_PATH_INDEX)
    private String categoryPath;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.LEVEL)
    private String level;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Category.MODIFIED)
    private Date modified;

    @Getter
    @Setter
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
}
