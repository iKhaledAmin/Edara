package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.model.dto.UserResponse;
import com.edara.edara.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRequest request);
    UserResponse toResponse(User entity);
}
