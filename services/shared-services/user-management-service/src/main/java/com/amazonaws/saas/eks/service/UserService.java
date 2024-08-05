package com.amazonaws.saas.eks.service;

import com.amazonaws.saas.eks.dto.requests.user.*;
import com.amazonaws.saas.eks.dto.responses.role.ListRolesResponse;
import com.amazonaws.saas.eks.dto.responses.role.RoleSummary;
import com.amazonaws.saas.eks.dto.responses.user.ListUserPermissionsResponse;
import com.amazonaws.saas.eks.dto.responses.user.ListUsersResponse;
import com.amazonaws.saas.eks.dto.responses.user.UserResponse;
import com.amazonaws.saas.eks.mapper.UserMapper;
import com.amazonaws.saas.eks.repository.PermissionRepository;
import com.amazonaws.saas.eks.repository.RoleRepository;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    // #region CRUD

    /**
     * Retrieve all the users for a single tenant
     * 
     * @param userPoolId the user pool identifier
     * @param request the pagination parameters
     * @return ListUsersResponse
     */
    public ListUsersResponse getUsers(String userPoolId, ListUsersRequestParams request) {
        ListUsersResponse result = new ListUsersResponse();

        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        ListUsersRequest listUsersRequest = new ListUsersRequest().withUserPoolId(userPoolId);

        if (request.getAttributesToGet().size() > 0) {
            listUsersRequest = listUsersRequest.withAttributesToGet(request.getAttributesToGet());
        }

        if (!StringUtils.isEmpty(request.getFilter())) {
            listUsersRequest = listUsersRequest.withFilter(request.getFilter());
        }

        if (request.getLimit() > 0) {
            listUsersRequest = listUsersRequest.withLimit(request.getLimit());
        }

        if (!StringUtils.isEmpty(request.getPaginationToken())) {
            listUsersRequest = listUsersRequest.withPaginationToken(request.getPaginationToken());
        }

        ListUsersResult response = cognitoClient.listUsers(listUsersRequest);

        for (UserType userType : response.getUsers()) {
            result.getUsers().add(UserMapper.INSTANCE.userResponseToUserSummary(new UserResponse(userType)));
        }
        result.setPaginationToken(response.getPaginationToken());

        return result;
    }

    /**
     * Retrieve one user by username
     * 
     * @param userPoolId the user pool identifier
     * @param username the username
     * @return UserResponse
     */
    public UserResponse getUser(String userPoolId, String username) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminGetUserRequest request = new AdminGetUserRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username);

        AdminGetUserResult response = cognitoClient.adminGetUser(request);

        return new UserResponse(response);
    }

    /**
     * Method to create a new user
     * 
     * @param userPoolId the user pool identifier
     * @param createUserRequest the user information and attributes
     * @return UserResponse
     */
    public UserResponse createUser(String tenantId, String userPoolId, CreateUserRequest createUserRequest) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        UserResponse user = UserMapper.INSTANCE.createUserRequestToUserResponse(createUserRequest);

        AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest()
                .withUserPoolId(userPoolId)
                .withUsername(user.getUsername())
                .withUserAttributes(user.getCognitoUserAttributes(tenantId));

        if (!StringUtils.isEmpty(createUserRequest.getTemporaryPassword())) {
            adminCreateUserRequest = adminCreateUserRequest
                    .withTemporaryPassword(createUserRequest.getTemporaryPassword());
        }

        AdminCreateUserResult createUserResult = cognitoIdentityProvider.adminCreateUser(adminCreateUserRequest);

        UserType cognitoUser = createUserResult.getUser();

        logger.info("Successfully created a new Cognito User: " + cognitoUser.getUsername());

        return new UserResponse(cognitoUser);
    }

    /**
     * Method to enable or disable tenant user
     * 
     * @param userPoolId the user pool identifier
     * @param username the username
     * @param enabled the status of the user
     */
    public void changeUserStatus(String userPoolId, String username, Boolean enabled) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        if (enabled) {
            AdminEnableUserRequest adminEnableUserRequest = new AdminEnableUserRequest();
            adminEnableUserRequest.setUsername(username);
            adminEnableUserRequest.setUserPoolId(userPoolId);

            cognitoIdentityProvider.adminEnableUser(adminEnableUserRequest);
        } else {
            AdminDisableUserRequest adminDisableUserRequest = new AdminDisableUserRequest();
            adminDisableUserRequest.setUsername(username);
            adminDisableUserRequest.setUserPoolId(userPoolId);

            cognitoIdentityProvider.adminDisableUser(adminDisableUserRequest);
        }
    }

    /**
     * Update a user's information and attributes
     * 
     * @param userPoolId the user pool identifier
     * @param username the username
     * @param updateUserRequest the user information and attributes to update
     * @return UserResponse
     */
    public UserResponse updateUser(String userPoolId, String username, UpdateUserRequest updateUserRequest) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        UserResponse user = UserMapper.INSTANCE.updateUserRequestToUserResponse(updateUserRequest);

        AdminUpdateUserAttributesRequest adminUpdateUserRequest = new AdminUpdateUserAttributesRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username)
                .withUserAttributes(user.getCognitoUserAttributes(""));

        cognitoIdentityProvider.adminUpdateUserAttributes(adminUpdateUserRequest);

        logger.info("Successfully updated Cognito User: " + username);
        return getUser(userPoolId, username);
    }

    /**
     * Method to delete a user
     * 
     * @param userPoolId the user pool identifier
     * @param username the username
     */
    public void deleteUser(String userPoolId, String username) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username);

        cognitoIdentityProvider.adminDeleteUser(adminDeleteUserRequest);
    }

    /**
     * Resets the specified user's password in a user pool as an administrator.
     *
     * @param userPoolId the user pool identifier
     * @param username the username
     */
    public void resetPassword(String userPoolId, String username) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminResetUserPasswordRequest request = new AdminResetUserPasswordRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username);

        cognitoClient.adminResetUserPassword(request);
    }

    /**
     * Sets the specified user's password in a user pool as an administrator.
     *
     * @param userPoolId the user pool identifier
     * @param username the username
     */
    public void setPassword(String userPoolId, String username, SetUserPasswordRequest setUserPasswordRequest) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminSetUserPasswordRequest request = new AdminSetUserPasswordRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username)
                .withPassword(setUserPasswordRequest.getPassword())
                .withPermanent(setUserPasswordRequest.isPermanent());

        cognitoClient.adminSetUserPassword(request);
    }
    // #endregion

    // #region Roles
    public void addRoleToUser(String userPoolId, String username, String role) {
        AWSCognitoIdentityProvider provider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username)
                .withGroupName(role);

        provider.adminAddUserToGroup(request);
    }

    public void removeRoleFromUser(String userPoolId, String username, String role) {
        AWSCognitoIdentityProvider provider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminRemoveUserFromGroupRequest request = new AdminRemoveUserFromGroupRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username)
                .withGroupName(role);

        provider.adminRemoveUserFromGroup(request);
    }

    public ListRolesResponse listRolesForUser(String userPoolId, String username) {
        ListRolesResponse result = new ListRolesResponse();

        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        AdminListGroupsForUserRequest request = new AdminListGroupsForUserRequest()
                .withUserPoolId(userPoolId)
                .withUsername(username);

        AdminListGroupsForUserResult response = cognitoClient.adminListGroupsForUser(request);

        List<RoleSummary> roles = new ArrayList<>();
        for (GroupType groupType : response.getGroups()) {
            roles.add(new RoleSummary(groupType.getGroupName(), groupType.getDescription()));
        }

        result.setRoles(roles);

        return result;
    }

    public void updateRolesToUser(String userPoolId, String username, UpdateUserRolesRequest updateUserRolesRequest) {
        ListRolesResponse rolesResponse = listRolesForUser(userPoolId, username);

        List<RoleSummary> toRemove = rolesResponse.getRoles();
        List<String> toAdd = updateUserRolesRequest.getRoles();
        
        boolean found;
        int i = 0;
        while (i < toAdd.size()) {
            found = false;
            for (int j = 0; j < toRemove.size(); j++) {
                found = toAdd.get(i).equals(toRemove.get(j).getName());
                if (found) {
                    toAdd.remove(i);
                    toRemove.remove(j);
                    break;
                }
            }
            if (!found) {
                i++;
            }
        }
        
        for (i = 0; i < toAdd.size(); i++) {
            addRoleToUser(userPoolId, username, toAdd.get(i));
        }
        
        for (i = 0; i < toRemove.size(); i++) {
            removeRoleFromUser(userPoolId, username, toRemove.get(i).getName());
        }
    }

    public ListUserPermissionsResponse listPermissionsForUser(String tenantId, String userPoolId, String username) {
        ListUserPermissionsResponse response = new ListUserPermissionsResponse();

        ListRolesResponse rolesResponse = listRolesForUser(userPoolId, username);

        Map<String, Set<String>> permissionCategories = new HashMap<>();

        for (RoleSummary role : rolesResponse.getRoles()) {
            Map<String, Set<String>> rolePermissionCategories = roleRepository
                    .getPermissionsCategories(tenantId, userPoolId, role.getName());
            rolePermissionCategories.forEach((k, v) -> permissionCategories.merge(k, v, (v1, v2) -> {
                Set<String> set = new TreeSet<>(v1);
                set.addAll(v2);
                return set;
            }));
        }

        response.setPermissions(permissionCategories);

        return response;
    }
    // #endregion
}