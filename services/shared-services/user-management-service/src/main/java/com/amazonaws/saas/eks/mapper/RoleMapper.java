package com.amazonaws.saas.eks.mapper;

import com.amazonaws.saas.eks.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


import com.amazonaws.saas.eks.dto.responses.role.RoleResponse;
import com.amazonaws.saas.eks.dto.responses.role.RoleSummary;


@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleSummary roleResponseToRoleSummary(RoleResponse role);

    Role roleResponseToRole(RoleResponse role);

    RoleResponse roleToRoleResponse(Role role);
}
