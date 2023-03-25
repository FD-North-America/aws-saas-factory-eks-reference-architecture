package com.amazonaws.saas.eks.service;

import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.saas.eks.dto.requests.role.UpdateRoleRequest;
import com.amazonaws.saas.eks.mapper.RoleMapper;
import com.amazonaws.saas.eks.model.PermissionsCategory;
import com.amazonaws.saas.eks.model.Role;
import com.amazonaws.services.cognitoidp.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.saas.eks.dto.requests.role.CreateRoleRequest;
import com.amazonaws.saas.eks.dto.requests.role.ListRolesRequestParams;
import com.amazonaws.saas.eks.dto.requests.user.ListUsersWithRoleRequestParams;
import com.amazonaws.saas.eks.dto.responses.role.ListRolesResponse;
import com.amazonaws.saas.eks.dto.responses.user.ListUsersResponse;
import com.amazonaws.saas.eks.dto.responses.role.RoleResponse;
import com.amazonaws.saas.eks.dto.responses.role.RoleSummary;
import com.amazonaws.saas.eks.dto.responses.user.UserResponse;
import com.amazonaws.saas.eks.dto.responses.user.UserSummary;
import com.amazonaws.saas.eks.mapper.UserMapper;
import com.amazonaws.saas.eks.repository.PermissionRepository;
import com.amazonaws.saas.eks.repository.RoleRepository;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

@Service
public class RoleService {
    private static final Logger logger = LogManager.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    // #region CRUD

    /**
     * Retrieve all user's roles
     * 
     * @param userPoolId the user pool identifier
     * @param request the pagination parameters
     * @return ListUsersResponse
     */
    public ListRolesResponse getAll(String userPoolId, ListRolesRequestParams request) {
        ListRolesResponse result = new ListRolesResponse();

        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        ListGroupsRequest listGroupsRequest = new ListGroupsRequest().withUserPoolId(userPoolId);

        if (request.getLimit() > 0) {
            listGroupsRequest = listGroupsRequest.withLimit(request.getLimit());
        }

        if (!StringUtils.isEmpty(request.getNextToken())) {
            listGroupsRequest = listGroupsRequest.withNextToken(request.getNextToken());
        }

        ListGroupsResult response = cognitoClient.listGroups(listGroupsRequest);

        for (GroupType groupType : response.getGroups()) {
            result.getRoles().add(new RoleSummary(groupType.getGroupName(), groupType.getDescription()));
        }
        result.setNextToken(response.getNextToken());

        return result;
    }

    /**
     * Retrieve the role details
     *
     * @param tenantId the tenant identifier
     * @param userPoolId the user pool identifier
     * @param roleName the name of the role
     * @return RoleResponse
     */
    public RoleResponse get(String tenantId, String userPoolId, String roleName) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        GetGroupRequest request = new GetGroupRequest()
                .withUserPoolId(userPoolId)
                .withGroupName(roleName);

        GetGroupResult response = cognitoClient.getGroup(request);

        GroupType groupType = response.getGroup();

        RoleResponse role = new RoleResponse(groupType);

        role.setPermissionsCategories(roleRepository.getPermissionsCategories(tenantId, userPoolId, roleName));

        return role;
    }

    /**
     * Retrieve all users with a specified role.
     * 
     * @param userPoolId the user pool identifier
     * @param roleName the name of the role
     * @param requestParams the pagination parameters
     * @return ListUsersResponse
     */
    public ListUsersResponse getUsersWithRole(String userPoolId, String roleName,
            ListUsersWithRoleRequestParams requestParams) {
        ListUsersResponse result = new ListUsersResponse();

        ListUsersInGroupResult response = listUsersInGroup(userPoolId, roleName, requestParams);

        UserResponse user;
        UserSummary userSummary;
        for (UserType userType : response.getUsers()) {
            user = new UserResponse(userType);

            userSummary = UserMapper.INSTANCE.userResponseToUserSummary(user);

            result.getUsers().add(userSummary);
        }
        result.setPaginationToken(response.getNextToken());

        return result;
    }

    /**
     * Create a new role
     *
     * @param tenantId the tenant identifier
     * @param userPoolId the user pool identifier
     * @param createRoleRequest the role name plus other data
     * @return RoleResponse
     */
    public RoleResponse create(String tenantId, String userPoolId, CreateRoleRequest createRoleRequest)
            throws Exception {
        // Verify all categories/permissions (if any submitted) exist
        verifyExistence(tenantId, userPoolId, createRoleRequest.getPermissionsCategories());
        // Create Cognito Group
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        CreateGroupRequest createGroupRequest = new CreateGroupRequest()
                .withUserPoolId(userPoolId)
                .withGroupName(createRoleRequest.getName())
                .withDescription(createRoleRequest.getDescription());

        CreateGroupResult createGroupResult = cognitoClient.createGroup(createGroupRequest);

        GroupType cognitoGroup = createGroupResult.getGroup();

        logger.info("Successfully created a new Cognito Group: " + cognitoGroup.getGroupName());
        // Insert New Role In DB
        Role role = new Role(tenantId, cognitoGroup);
        role.setPermissionsCategories(createRoleRequest.getPermissionsCategories());
        roleRepository.insert(role);

        return RoleMapper.INSTANCE.roleToRoleResponse(role);
    }

    /**
     * Update a role
     *
     * @param tenantId the tenant identifier
     * @param userPoolId the user pool identifier
     * @param updateRoleRequest the role data to be updated
     * @return RoleResponse
     */
    public RoleResponse update(String tenantId, String userPoolId, String roleName,
                                   UpdateRoleRequest updateRoleRequest) throws Exception {
        // Validate all permissions (if any submitted) exist
        if (updateRoleRequest.getPermissionsCategories() != null
                && !updateRoleRequest.getPermissionsCategories().isEmpty()) {
            verifyExistence(tenantId, userPoolId, updateRoleRequest.getPermissionsCategories());
        }
        // Update Cognito Group
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        UpdateGroupRequest updateGroupRequest = new UpdateGroupRequest()
                .withUserPoolId(userPoolId)
                .withGroupName(roleName);

        if (updateRoleRequest.getDescription() != null) {
            updateGroupRequest = updateGroupRequest.withDescription(updateRoleRequest.getDescription());
        }

        UpdateGroupResult updateGroupResult = cognitoClient.updateGroup(updateGroupRequest);

        GroupType cognitoGroup = updateGroupResult.getGroup();

        logger.info("Successfully updated the Cognito Group: " + cognitoGroup.getGroupName());
        // Update Role In DB
        Role role = new Role(tenantId, cognitoGroup);
        role.setPermissionsCategories(updateRoleRequest.getPermissionsCategories());
        role = roleRepository.update(role);

        return RoleMapper.INSTANCE.roleToRoleResponse(role);
    }

    /**
     * Delete a role
     * 
     * @param tenantId the tenant identifier
     * @param userPoolId the user pool identifier
     * @param roleName the name of the role
     * @return Boolean
     */
    public void delete(String tenantId, String userPoolId, String roleName) throws Exception {
        // Check if there is any user with this role to avoid deleting it
        List<String> usersWithThisRole = listUsernamesInGroup(userPoolId, roleName);
        if (usersWithThisRole != null && !usersWithThisRole.isEmpty()) {
            String msg = String.format("There are users associated to this role. Users: %s. TenantId: %s, UserPoolId: %s",
                    usersWithThisRole, tenantId, userPoolId);
            logger.error(msg);
            throw new Exception(msg);
        }
        // Delete Cognito Group
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        DeleteGroupRequest request = new DeleteGroupRequest()
                .withUserPoolId(userPoolId)
                .withGroupName(roleName);

        DeleteGroupResult result = cognitoClient.deleteGroup(request);
        if (result.getSdkHttpMetadata().getHttpStatusCode() != 200) {
            String msg = String.format("The cognito group %s couldn't be deleted.", roleName);
            logger.error(msg);
            throw new Exception(msg);
        }
        logger.info("Successfully deleted the Cognito Group: " + roleName);
        // Delete Role In DB
        roleRepository.delete(new Role(tenantId, userPoolId, roleName));
    }
    // #endregion

    private void verifyExistence(String tenantId, String userPoolId, Map<String, Set<String>> permissionsCategories)
            throws Exception {
        List<PermissionsCategory> models = permissionRepository.batchLoad(tenantId, userPoolId,
                permissionsCategories.keySet());

        if (models == null) {
            throw new Exception("All categories not found.");
        }

        List<String> missingCategories = new ArrayList<>();
        List<String> missingPermissions = new ArrayList<>();

        boolean categoryFound;
        for (Map.Entry<String, Set<String>> e: permissionsCategories.entrySet()) {
            categoryFound = false;
            for (PermissionsCategory model: models) {
                if (e.getKey().equals(model.getCategoryName())) {
                    categoryFound = true;
                    for (String p: e.getValue()) {
                        if (!model.getPermissions().containsKey(p)) {
                            missingPermissions.add(p);
                        }
                    }
                    break;
                }
            }
            if (!categoryFound) {
                missingCategories.add(e.getKey());
            }
        }

        if (missingCategories.size() > 0) {
            throw new Exception(String.format("Categories not found: %s", missingCategories));
        }

        if (missingPermissions.size() > 0) {
            throw new Exception(String.format("Permissions not found: %s", missingPermissions));
        }
    }

    private ListUsersInGroupResult listUsersInGroup(String userPoolId, String roleName,
                                                    ListUsersWithRoleRequestParams requestParams) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        ListUsersInGroupRequest request = new ListUsersInGroupRequest()
                .withUserPoolId(userPoolId)
                .withGroupName(roleName);

        if (requestParams.getLimit() > 0) {
            request = request.withLimit(requestParams.getLimit());
        }

        if (!StringUtils.isEmpty(requestParams.getNextToken())) {
            request = request.withNextToken(requestParams.getNextToken());
        }

        return cognitoClient.listUsersInGroup(request);
    }

    private List<String> listUsernamesInGroup(String userPoolId, String roleName) {
        ListUsersInGroupResult result = listUsersInGroup(userPoolId, roleName, new ListUsersWithRoleRequestParams());
        if (result != null) {
            return result.getUsers().stream().map(UserType::getUsername).collect(Collectors.toList());
        }
        return null;
    }
}
