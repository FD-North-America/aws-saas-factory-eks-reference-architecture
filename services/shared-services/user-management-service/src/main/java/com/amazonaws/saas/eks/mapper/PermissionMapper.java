package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.dto.requests.permission.CreatePermissionRequest;
import com.amazonaws.saas.eks.dto.requests.permission.UpdatePermissionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.amazonaws.saas.eks.dto.responses.permission.PermissionResponse;
import com.amazonaws.saas.eks.model.Permission;


@Mapper
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionResponse permissionToPermissionResponse(Permission permission);

    Permission createPermissionRequestToPermission(CreatePermissionRequest request);

    Permission updatePermissionRequestToPermission(UpdatePermissionRequest request);
}
