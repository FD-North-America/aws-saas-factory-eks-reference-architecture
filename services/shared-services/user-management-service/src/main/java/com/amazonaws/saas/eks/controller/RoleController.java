package com.amazonaws.saas.eks.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.role.UpdateRoleRequest;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.saas.eks.dto.requests.role.CreateRoleRequest;
import com.amazonaws.saas.eks.dto.requests.role.ListRolesRequestParams;
import com.amazonaws.saas.eks.dto.requests.user.ListUsersWithRoleRequestParams;
import com.amazonaws.saas.eks.dto.responses.role.ListRolesResponse;
import com.amazonaws.saas.eks.dto.responses.user.ListUsersResponse;
import com.amazonaws.saas.eks.dto.responses.role.RoleResponse;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RoleController {
    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Autowired
    private RoleService service;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    /**
     * Retrieve all user's roles.
     * 
     * @param requestParams the query string parameters
     * @param request the http servlet request
     * @return ListRolesResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_READ + "')")
    @GetMapping(value = "roles", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListRolesResponse getAll(ListRolesRequestParams requestParams, HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            return this.service.getAll(userPoolId, requestParams);
        } catch (Exception e) {
            logger.error("UserManagement get-roles operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve the role details.
     * 
     * @param roleName the name of the role
     * @param request the http servlet request
     * @return RoleResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_READ + "')")
    @GetMapping(value = "roles/{roleName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public RoleResponse get(@PathVariable("roleName") String roleName, HttpServletRequest request) {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            return this.service.get(tenantUser.getTenantId(), tenantUser.getUserPoolId(), roleName);
        } catch (Exception e) {
            logger.error("UserManagement get-role operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve all users with a specified role.
     * 
     * @param roleName the name of the role
     * @param requestParams the query string parameters
     * @param request the http servlet request
     * @return ListUsersResponse
     */
    @PreAuthorize("hasAuthority('" + Permission.MANAGE_ROLES_READ + "') && hasAuthority('" + Permission.MANAGE_USERS_READ + "')")
    @GetMapping(value = "roles/{roleName}/users", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListUsersResponse getUsersWithRole(@PathVariable("roleName") String roleName,
                                              ListUsersWithRoleRequestParams requestParams,
                                              HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            return this.service.getUsersWithRole(userPoolId, roleName, requestParams);
        } catch (Exception e) {
            logger.error("UserManagement get-users-with-role operation failed.", e);
            throw e;
        }
    }

    /**
     * Create a new role.
     * 
     * @param createRoleRequest the new role name plus other data
     * @param request the http servlet request
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_CREATE + "')")
    @PostMapping(value = "roles", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> create(@RequestBody @Valid CreateRoleRequest createRoleRequest,
                                    HttpServletRequest request) throws Exception {
        try {
            createRoleRequest.transformName();
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            RoleResponse newRole = this.service.create(tenantUser.getTenantId(), tenantUser.getUserPoolId(),
                    createRoleRequest);
            URI userURI = URI.create("/roles/" + newRole.getName());
            return ResponseEntity.created(userURI).body(newRole);
        } catch (Exception e) {
            logger.error("UserManagement create-role operation failed.", e);
            throw e;
        }
    }

    /**
     * Update a role.
     * 
     * @param roleName the name of the role
     * @param updateRoleRequest the role data to update
     * @param request the http servlet request
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_UPDATE + "')")
    @PutMapping(value = "roles/{roleName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> update(@PathVariable("roleName") String roleName,
                                    @RequestBody @Valid UpdateRoleRequest updateRoleRequest,
                                    HttpServletRequest request) throws Exception {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            RoleResponse role = this.service.update(tenantUser.getTenantId(), tenantUser.getUserPoolId(), roleName,
                    updateRoleRequest);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            logger.error("UserManagement update-role operation failed.", e);
            throw e;
        }
    }

    /**
     * Delete a role.
     * 
     * @param roleName the name of the role
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_ROLES_DELETE + "')")
    @DeleteMapping(value = "roles/{roleName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void delete(@PathVariable("roleName") String roleName, HttpServletRequest request)
            throws Exception {
        try {
            TenantUser tenantUser = jwtAuthManager.getTenantUser();
            this.service.delete(tenantUser.getTenantId(), tenantUser.getUserPoolId(), roleName);
        } catch (Exception e) {
            logger.error("UserManagement delete-role operation failed.", e);
            throw e;
        }
    }
}
