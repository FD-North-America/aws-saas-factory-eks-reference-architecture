package com.amazonaws.saas.eks.repository;

import java.util.*;

import com.amazonaws.saas.eks.exception.EntityNotFoundException;
import com.amazonaws.saas.eks.exception.EntityExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.amazonaws.saas.eks.model.Role;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class RoleRepository {
    private static final Logger logger = LogManager.getLogger(RoleRepository.class);

    @Autowired
    private DynamoDBMapper mapper;

    /**
     * Retrieve all roles for a tenant and a user pool.
     * 
     * @param tenantId the tenant identifier
     * @param userPoolId the user pool identifier
     * @return List<Role>
     */
    public List<Role> getAll(String tenantId, String userPoolId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":tenantId", new AttributeValue().withS(tenantId));
         eav.put(":userPoolId", new AttributeValue().withS(userPoolId));

        DynamoDBQueryExpression<Role> query = new DynamoDBQueryExpression<Role>()
                .withKeyConditionExpression("TenantId = :tenantId and begins_with(Id, :userPoolId)")
                .withExpressionAttributeValues(eav);

        return mapper.query(Role.class, query);
    }

    public Role get(String tenantId, String userPoolId, String roleName) throws EntityNotFoundException {
        return get(new Role(tenantId, userPoolId, roleName));
    }

    public Role get(Role role) throws EntityNotFoundException {
        Role model = mapper.load(role);
        if (model == null) {
            throw new EntityNotFoundException("Role", role.getName(), role.getTenantId(), role.getUserPoolId());
        }
        return model;
    }

    public Map<String, Set<String>> getPermissionsCategories(String tenantId, String userPoolId, String roleName)
            throws EntityNotFoundException {
        Role role = get(tenantId, userPoolId, roleName);
        return role.getPermissionsCategories();
    }

    public Set<String> getPermissions(String tenantId, String userPoolId, String roleName)
            throws EntityNotFoundException {
        Set<String> permissions = new HashSet<>();

        Role role = get(tenantId, userPoolId, roleName);
        for (Set<String> p: role.getPermissionsCategories().values()) {
            permissions.addAll(p);
        }

        return permissions;
    }

    public void insert(Role role) throws EntityExistsException {
        try {
            if (get(role) != null) {
                throw new EntityExistsException("Role", role.getName(), role.getTenantId(), role.getUserPoolId());
            }
        } catch (EntityNotFoundException ex) {
            // Pass-through
        }

        mapper.save(role);
    }

    public Role update(Role role) throws EntityNotFoundException {
        Role model = get(role);

        if (role.getDescription() != null) {
            model.setDescription(role.getDescription());
        }

        if (role.getPermissionsCategories() != null) {
            model.setPermissionsCategories(role.getPermissionsCategories());
        }

        model.setModified(role.getModified());

        mapper.save(model);

        return model;
    }

    public void delete(Role role) throws EntityNotFoundException {
        Role model = get(role);

        mapper.delete(model);
    }
}
