package com.amazonaws.saas.eks.repository;

import java.util.*;

import com.amazonaws.saas.eks.exception.EntityExistsException;
import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.model.PermissionsCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class PermissionRepository {
    private static final Logger logger = LogManager.getLogger(PermissionRepository.class);

    @Autowired
    private DynamoDBMapper mapper;

    public List<PermissionsCategory> getAll(String tenantId, String userPoolId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":tenantId", new AttributeValue().withS(tenantId));
        eav.put(":userPoolId", new AttributeValue().withS(userPoolId));

        DynamoDBQueryExpression<PermissionsCategory> query = new DynamoDBQueryExpression<PermissionsCategory>()
                .withKeyConditionExpression("TenantId = :tenantId and begins_with(Id, :userPoolId)")
                .withExpressionAttributeValues(eav);

        return mapper.query(PermissionsCategory.class, query);
    }

    public PermissionsCategory get(String tenantId, String userPoolId, String categoryName)
            throws EntityNotFoundException {
        PermissionsCategory model = mapper.load(new PermissionsCategory(tenantId, userPoolId, categoryName));
        if (model == null) {
            throw new EntityNotFoundException("Category", categoryName, tenantId, userPoolId);
        }
        return model;
    }

    public Permission getPermission(String tenantId, String userPoolId, PermissionsCategory permissionsCategory,
                                    String permissionName)
            throws EntityNotFoundException {
        Permission permission = permissionsCategory.getPermissions().get(permissionName);
        if (permission == null) {
            throw new EntityNotFoundException("Category->Permission",
                    permissionsCategory.getCategoryName() + "->" + permissionName, tenantId, userPoolId);
        }
        return permission;
    }

    public PermissionsCategory insert(String tenantId, String userPoolId, String categoryName, Permission permission)
            throws EntityExistsException {
        PermissionsCategory model;
        try {
            model = get(tenantId, userPoolId, categoryName);
            if (model.getPermissions().get(permission.getName()) != null) {
                throw new EntityExistsException("Category->Permission", categoryName + "->" + permission.getName(),
                        tenantId, userPoolId);
            }
        } catch (EntityNotFoundException ex) {
            model = new PermissionsCategory(tenantId, userPoolId, categoryName);
            model.setCreated(permission.getCreated());
            model.setPermissions(new HashMap<>());
        }

        model.getPermissions().put(permission.getName(), permission);
        model.setModified(permission.getCreated());

        mapper.save(model);

        return model;
    }

    public PermissionsCategory update(String tenantId, String userPoolId, String categoryName, Permission newPermission)
            throws EntityNotFoundException {
        PermissionsCategory model = get(tenantId, userPoolId, categoryName);

        Permission oldPermission = getPermission(tenantId, userPoolId, model, newPermission.getName());

        if (newPermission.getDescription() != null) {
            oldPermission.setDescription(newPermission.getDescription());
        }

        if (newPermission.getLabel() != null) {
            oldPermission.setLabel(newPermission.getLabel());
        }

        if (newPermission.getGroup() != null) {
            oldPermission.setGroup(newPermission.getGroup());
        }

        oldPermission.setModified(newPermission.getModified());
        model.setModified(newPermission.getModified());

        mapper.save(model);

        return model;
    }

    public void delete(String tenantId, String userPoolId, String categoryName, String permissionName)
            throws EntityNotFoundException {
        PermissionsCategory model = get(tenantId, userPoolId, categoryName);

        Permission oldPermission = getPermission(tenantId, userPoolId, model, permissionName);

        model.getPermissions().remove(oldPermission.getName());

        if (model.getPermissions().size() > 0) {
            model.setModified(new Date());
            mapper.save(model);
        } else {
            mapper.delete(model);
        }
    }

    public List<PermissionsCategory> batchLoad(String tenantId, String userPoolId, Set<String> categories) {
        List<PermissionsCategory> models = new ArrayList<>();
        for (String category: categories) {
            models.add(new PermissionsCategory(tenantId, userPoolId, category));
        }

        Map<String, List<Object>> batchResult = mapper.batchLoad(models);

        if (batchResult.containsKey(PermissionsCategory.TABLE_NAME)) {
            List<Object> items = batchResult.get(PermissionsCategory.TABLE_NAME);
            if (items != null) {
                models = (List<PermissionsCategory>) (List<?>) items;
                return models;
            }
        }
        return null;
    }
}
