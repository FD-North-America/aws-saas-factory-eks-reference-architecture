package com.amazonaws.saas.eks.controller;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.permission.CreatePermissionRequest;
import com.amazonaws.saas.eks.dto.requests.permission.UpdatePermissionRequest;
import com.amazonaws.saas.eks.dto.responses.permission.ListPermissionsResponse;
import com.amazonaws.saas.eks.dto.responses.permission.PermissionResponse;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.service.PermissionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class PermissionController {
    private static final Logger logger = LogManager.getLogger(PermissionController.class);

    @Autowired
    private JwtAuthManager jwtAuthManager;

    @Autowired
    private PermissionService service;

    /**
     * Retrieve all existing permissions.
     *
     * @param request the http servlet request information
     * @return ListPermissionResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_READ + "')")
    @GetMapping(value = "permissions", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListPermissionsResponse getAll(HttpServletRequest request) {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            return this.service.getAll(tenantUser.getTenantId(), tenantUser.getUserPoolId());
        } catch (Exception e) {
            logger.error("UserManagement get-permissions operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve the permission details.
     *
     * @param categoryName the category of the permission
     * @param permissionName the name of the permission
     * @return PermissionResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_READ + "')")
    @GetMapping(value = "permissions/{categoryName}/{permissionName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public PermissionResponse get(@PathVariable("categoryName") String categoryName,
                                  @PathVariable("permissionName") String permissionName) {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            return this.service.get(tenantUser.getTenantId(), tenantUser.getUserPoolId(), categoryName, permissionName);
        } catch (Exception e) {
            logger.error("UserManagement get-permission operation failed.", e);
            throw e;
        }
    }

    /**
     * Create a new permission.
     *
     * @param createPermissionRequest the permission's name plus any other information
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_CREATE + "')")
    @PostMapping(value = "permissions", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> create(@RequestBody @Valid CreatePermissionRequest createPermissionRequest)
            throws Exception {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            PermissionResponse newPermission = this.service.create(tenantUser.getTenantId(), tenantUser.getUserPoolId(),
                    createPermissionRequest);
            URI userURI = URI.create("/permissions/" + newPermission.getName());
            return ResponseEntity.created(userURI).body(newPermission);
        } catch (Exception e) {
            logger.error("UserManagement create-permission operation failed.", e);
            throw e;
        }
    }

    /**
     * Update the permission's information.
     *
     * @param categoryName the category of the permission
     * @param permissionName the name of the permission
     * @param updatePermissionRequest the permission's information to update
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_UPDATE + "')")
    @PutMapping(value = "permissions/{categoryName}/{permissionName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> update(@PathVariable("categoryName") String categoryName,
                                              @PathVariable("permissionName") String permissionName,
                                              @RequestBody @Valid UpdatePermissionRequest updatePermissionRequest)
            throws Exception {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            PermissionResponse response = this.service.update(tenantUser.getTenantId(), tenantUser.getUserPoolId(),
                    categoryName, permissionName, updatePermissionRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("UserManagement update-permission operation failed.", e);
            throw e;
        }
    }

    /**
     * Delete the permission
     *
     * @param categoryName the category of the permission
     * @param permissionName the name of the permission
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_DELETE + "')")
    @DeleteMapping(value = "permissions/{categoryName}/{permissionName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable("categoryName") String categoryName,
                                    @PathVariable("permissionName") String permissionName)
            throws Exception {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            this.service.delete(tenantUser.getTenantId(), tenantUser.getUserPoolId(), categoryName, permissionName);
        } catch (Exception e) {
            logger.error("UserManagement delete-permission operation failed.", e);
            throw e;
        }
    }
}
