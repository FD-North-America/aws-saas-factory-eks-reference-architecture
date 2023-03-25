package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.permission.CreatePermissionRequest;
import com.amazonaws.saas.eks.dto.requests.permission.UpdatePermissionRequest;
import com.amazonaws.saas.eks.dto.responses.permission.ListPermissionsResponse;
import com.amazonaws.saas.eks.dto.responses.permission.PermissionResponse;
import com.amazonaws.saas.eks.mapper.PermissionMapper;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.model.PermissionsCategory;
import com.amazonaws.saas.eks.model.Role;
import com.amazonaws.saas.eks.repository.PermissionRepository;
import com.amazonaws.saas.eks.repository.RoleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PermissionService {
    private static final Logger logger = LogManager.getLogger(PermissionService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public ListPermissionsResponse getAll(String tenantId, String userPoolId) {
        ListPermissionsResponse response = new ListPermissionsResponse();

        List<PermissionsCategory> permissionsCategories = permissionRepository.getAll(tenantId, userPoolId);

        PermissionResponse permissionResponse;
        for (PermissionsCategory model: permissionsCategories) {
            for (Permission model2: model.getPermissions().values()) {
                permissionResponse = PermissionMapper.INSTANCE.permissionToPermissionResponse(model2);
                permissionResponse.setCategory(model.getCategoryName());
                response.getPermissions().add(permissionResponse);
            }
        }

        return response;
    }

    public PermissionResponse get(String tenantId, String userPoolId, String categoryName, String permissionName) {
        PermissionsCategory permissionsCategory = permissionRepository.get(tenantId, userPoolId, categoryName);
        return getResponse(tenantId, userPoolId, permissionsCategory, permissionName);
    }

    public PermissionResponse create(String tenantId, String userPoolId, CreatePermissionRequest request) {
        Permission permission = PermissionMapper.INSTANCE.createPermissionRequestToPermission(request);
        permission.setCreated(new Date());
        permission.setModified(permission.getCreated());

        PermissionsCategory permissionsCategory = permissionRepository.insert(tenantId, userPoolId,
                request.getCategory(), permission);

        return getResponse(tenantId, userPoolId, permissionsCategory, permission.getName());
    }

    public PermissionResponse update(String tenantId, String userPoolId, String categoryName, String permissionName,
                                     UpdatePermissionRequest request) throws Exception {
        Permission model = PermissionMapper.INSTANCE.updatePermissionRequestToPermission(request);
        model.setName(permissionName);
        model.setModified(new Date());

        PermissionsCategory permissionsCategory = permissionRepository.update(tenantId, userPoolId, categoryName, model);

        return getResponse(tenantId, userPoolId, permissionsCategory, permissionName);
    }

    public void delete(String tenantId, String userPoolId, String categoryName, String permissionName)
            throws Exception {
        // Check if there is any Role with this Permission to avoid deleting it
        List<Role> roles = roleRepository.getAll(tenantId, userPoolId);
        for (Role r: roles) {
            if (r.getPermissionsCategories() != null
                    && r.getPermissionsCategories().containsKey(categoryName)
                    && r.getPermissionsCategories().get(categoryName).contains(permissionName)) {
                String msg = String.format("There is at least one role with this permission associated to. " +
                                "Role: %s. TenantId: %s, UserPoolId: %s", r.getName(), tenantId, userPoolId);
                logger.error(msg);
                throw new Exception(msg);
            }
        }
        permissionRepository.delete(tenantId, userPoolId, categoryName, permissionName);
    }

    private PermissionResponse getResponse(String tenantId, String userPoolId, PermissionsCategory permissionsCategory,
                                           String permissionName) {
        Permission permission = permissionRepository.getPermission(tenantId, userPoolId, permissionsCategory,
                permissionName);
        PermissionResponse permissionResponse = PermissionMapper.INSTANCE.permissionToPermissionResponse(permission);
        permissionResponse.setCategory(permissionsCategory.getCategoryName());
        return permissionResponse;
    }
}
