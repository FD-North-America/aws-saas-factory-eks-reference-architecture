package com.amazonaws.saas.eks.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.amazonaws.saas.eks.auth.JwtAuthManager;
import com.amazonaws.saas.eks.auth.dto.TenantUser;
import com.amazonaws.saas.eks.dto.requests.user.*;
import com.amazonaws.saas.eks.dto.responses.role.ListRolesResponse;
import com.amazonaws.saas.eks.dto.responses.user.ListUserPermissionsResponse;
import com.amazonaws.saas.eks.dto.responses.user.ListUsersResponse;
import com.amazonaws.saas.eks.dto.responses.user.UserResponse;
import com.amazonaws.saas.eks.model.Permission;
import com.amazonaws.saas.eks.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService service;

    @Autowired
    private JwtAuthManager jwtAuthManager;

    /**
     * Retrieve all users from the Cognito user pool.
     * 
     * @param requestParams the query string parameters
     * @param request the http servlet request
     * @return ListUsersResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_READ + "')")
    @GetMapping(value = "users", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListUsersResponse getUsers(ListUsersRequestParams requestParams, HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            return this.service.getUsers(userPoolId, requestParams);
        } catch (Exception e) {
            logger.error("UserManagement getUsers operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve one user from the Cognito user pool.
     * 
     * @param username the username
     * @param request the http servlet request
     * @return UserResponse
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_READ + "')")
    @GetMapping(value = "users/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public UserResponse getUser(@PathVariable("username") String username, HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            return this.service.getUser(userPoolId, username);
        } catch (Exception e) {
            logger.error("UserManagement getUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Create a new user in the Cognito user pool.
     * 
     * @param createUserRequest the user information and attributes
     * @param request the http servlet request
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_CREATE + "')")
    @PostMapping(value = "users", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest createUserRequest,
                                        HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            UserResponse newUser = this.service.createUser(userPoolId, createUserRequest);
            URI userURI = URI.create("/users/" + newUser.getUsername());
            return ResponseEntity.created(userURI).body(newUser);
        } catch (Exception e) {
            logger.error("UserManagement createUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Method to change user's status in the user pool.
     * 
     * @param username the username
     * @param status the status of the user
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PutMapping(value = "users/{username}/status", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void changeUserStatus(@PathVariable("username") String username, @RequestBody UpdateUserStatusRequest status,
                                 HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.changeUserStatus(userPoolId, username, status.getEnabled());
        } catch (Exception e) {
            logger.error("UserManagement changeUserStatus operation failed.", e);
            throw e;
        }
    }

    /**
     * Update user's information and attributes in the Cognito user pool.
     * 
     * @param username the username
     * @param updateUserRequest the user information and attributes to update
     * @param request the http servlet request
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PutMapping(value = "users/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> updateUser(@PathVariable("username") String username,
                                        @RequestBody UpdateUserRequest updateUserRequest,
                                        HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            UserResponse user = this.service.updateUser(userPoolId, username, updateUserRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("UserManagement updateUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Method to delete a user from the user pool.
     * 
     * @param username the username
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_DELETE + "')")
    @DeleteMapping(value = "users/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void deleteUser(@PathVariable("username") String username, HttpServletRequest request) {

        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.deleteUser(userPoolId, username);
        } catch (Exception e) {
            logger.error("UserManagement deleteUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve all user's roles
     * 
     * @param username the username
     * @param request the http servlet request
     */
    @PreAuthorize("hasAuthority('" + Permission.MANAGE_USERS_READ + "')")
    @GetMapping(value = "users/{username}/roles", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ListRolesResponse listRolesForUser(@PathVariable("username") String username, HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            return this.service.listRolesForUser(userPoolId, username);
        } catch (Exception e) {
            logger.error("UserManagement listRolesForUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Update roles to a user
     * 
     * @param username the username
     * @param updateUserRolesRequest the roles to be assigned
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PutMapping(value = "users/{username}/roles", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void updateRolesToUser(@PathVariable("username") String username,
                                  @RequestBody @Valid UpdateUserRolesRequest updateUserRolesRequest,
                                  HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.updateRolesToUser(userPoolId, username, updateUserRolesRequest);
        } catch (Exception e) {
            logger.error("UserManagement updateRolesToUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Add role to a user
     * 
     * @param username the username
     * @param roleName the role name to be assigned
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PostMapping(value = "users/{username}/roles/{roleName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void addRoleToUser(@PathVariable("username") String username, @PathVariable("roleName") String roleName,
            HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.addRoleToUser(userPoolId, username, roleName);
        } catch (Exception e) {
            logger.error("UserManagement addRoleToUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Remove role from a user
     * 
     * @param username the username
     * @param roleName the role name
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @DeleteMapping(value = "users/{username}/roles/{roleName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void removeRoleFromUser(@PathVariable("username") String username, @PathVariable("roleName") String roleName,
            HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.removeRoleFromUser(userPoolId, username, roleName);
        } catch (Exception e) {
            logger.error("UserManagement removeRoleFromUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Retrieve all user's permissions
     * 
     * @param username the username
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_READ + "', '" + Permission.MANAGE_ROLES_READ + "')" +
            "OR authentication.principal.username == #username")
    @GetMapping(value = "users/{username}/permissions", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> listPermissionsForUser(@PathVariable("username") String username,
                                                    HttpServletRequest request) {
        try {
            TenantUser tu = jwtAuthManager.getTenantUser();
            ListUserPermissionsResponse response = this.service.listPermissionsForUser(tu.getTenantId(),
                    tu.getUserPoolId(), username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("UserManagement listPermissionsForUser operation failed.", e);
            throw e;
        }
    }

    /**
     * Reset user's password
     *
     * @param username the username
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PostMapping(value = "users/{username}/reset-password", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void resetPassword(@PathVariable("username") String username, HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.resetPassword(userPoolId, username);
        } catch (Exception e) {
            logger.error("UserManagement resetPassword operation failed.", e);
            throw e;
        }
    }

    /**
     * Sets the specified user's password.
     *
     * @param username the username
     * @param setUserPasswordRequest the password and its permanent condition
     * @param request the http servlet request
     */
    @PreAuthorize("hasAnyAuthority('" + Permission.MANAGE_USERS_UPDATE + "')")
    @PostMapping(value = "users/{username}/set-password", produces = { MediaType.APPLICATION_JSON_VALUE })
    public void setPassword(@PathVariable("username") String username,
                            @RequestBody @Valid SetUserPasswordRequest setUserPasswordRequest,
                            HttpServletRequest request) {
        try {
            String userPoolId = jwtAuthManager.getUserPoolId();
            this.service.setPassword(userPoolId, username, setUserPasswordRequest);
        } catch (Exception e) {
            logger.error("UserManagement setPassword operation failed.", e);
            throw e;
        }
    }
}
