package com.amazonaws.saas.eks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.amazonaws.saas.eks.dto.requests.user.CreateUserRequest;
import com.amazonaws.saas.eks.dto.requests.user.UpdateUserRequest;
import com.amazonaws.saas.eks.dto.responses.user.UserResponse;
import com.amazonaws.saas.eks.dto.responses.user.UserSummary;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserSummary userResponseToUserSummary(UserResponse user);

    UserResponse createUserRequestToUserResponse(CreateUserRequest createUserRequest);

    UserResponse updateUserRequestToUserResponse(UpdateUserRequest updateUserRequest);
}
