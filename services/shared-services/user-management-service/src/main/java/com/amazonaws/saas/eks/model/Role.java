package com.amazonaws.saas.eks.model;

import java.util.*;

import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import lombok.Getter;
import lombok.Setter;

@DynamoDBTable(tableName = Role.TABLE_NAME)
public class Role {
    public static final String TABLE_NAME = "Role_v3";
    public static final String SORT_KEY_SEP = "#";
    public static final String TENANT_ID = "TenantId";
    public static final String ID = "Id";
    public static final String DESCRIPTION = "Description";
    public static final String CREATED = "Created";
    public static final String MODIFIED = "Modified";
    public static final String PERMISSIONS = "Permissions";

    public Role(){}

    public Role(String tenantId, String userPoolId, String roleName) {
        this.tenantId = tenantId;
        this.id = composeId(userPoolId, roleName);
    }

    public Role(String tenantId, GroupType groupType) {
        this.tenantId = tenantId;
        this.id = composeId(groupType.getUserPoolId(), groupType.getGroupName());
        this.description = groupType.getDescription();
        this.created = groupType.getCreationDate();
        this.modified = groupType.getLastModifiedDate();
    }

    @Getter
    @Setter
    @DynamoDBHashKey(attributeName = Role.TENANT_ID)
    private String tenantId;

    @Getter
    @Setter
    @DynamoDBRangeKey(attributeName = Role.ID)
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Role.DESCRIPTION)
    private String description;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Role.CREATED)
    private Date created;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Role.MODIFIED)
    private Date modified;

    @Getter
    @Setter
    @DynamoDBAttribute(attributeName = Role.PERMISSIONS)
    private Map<String, Set<String>> permissionsCategories = new HashMap<>();

    @DynamoDBIgnore
    public String getUserPoolId() {
        if (getId() == null || getId().isEmpty()) {
            return null;
        }
        return decomposeId(getId()).getKey();
    }

    @DynamoDBIgnore
    public String getName() {
        if (getId() == null || getId().isEmpty()) {
            return null;
        }
        return decomposeId(getId()).getValue();
    }

    public static String composeId(String userPoolId, String roleName) {
        return userPoolId + SORT_KEY_SEP + roleName;
    }

    public static Map.Entry<String, String> decomposeId(String id) {
        String[] components = id.split(SORT_KEY_SEP);
        return new AbstractMap.SimpleEntry<>(components[0], components[1]);
    }
}
