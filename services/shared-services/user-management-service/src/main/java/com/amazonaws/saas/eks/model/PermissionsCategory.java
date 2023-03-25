package com.amazonaws.saas.eks.model;

import com.amazonaws.saas.eks.converter.MapOfPermissionsConverter;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@DynamoDBTable(tableName = PermissionsCategory.TABLE_NAME)
public class PermissionsCategory {
    public static final String TABLE_NAME = "Permission_v3";
    public static final String SORT_KEY_SEP = "#";
    public static final String TENANT_ID = "TenantId";
    public static final String ID = "Id";
    public static final String PERMISSIONS = "Permissions";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";

    public PermissionsCategory(){}

    public PermissionsCategory(String tenantId, String userPoolId, String category) {
        this.tenantId = tenantId;
        this.id = composeId(userPoolId, category);
    }

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = PermissionsCategory.TENANT_ID)
    private String tenantId;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = PermissionsCategory.ID)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = PermissionsCategory.PERMISSIONS)
    @DynamoDBTypeConverted(converter = MapOfPermissionsConverter.class)
    private Map<String, Permission> permissions;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = PermissionsCategory.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = PermissionsCategory.MODIFIED)
    private Date modified;

    @DynamoDBIgnore
    public String getUserPoolId() {
        if (getId() == null || getId().isEmpty()) {
            return null;
        }
        return decomposeId(getId()).getKey();
    }

    @DynamoDBIgnore
    public String getCategoryName() {
        if (getId() == null || getId().isEmpty()) {
            return null;
        }
        return decomposeId(getId()).getValue();
    }

    public static String composeId(String userPoolId, String category) {
        return userPoolId + SORT_KEY_SEP + category;
    }

    public static Map.Entry<String, String> decomposeId(String id) {
        String[] components = id.split(SORT_KEY_SEP);
        return new AbstractMap.SimpleEntry<>(components[0], components[1]);
    }
}
