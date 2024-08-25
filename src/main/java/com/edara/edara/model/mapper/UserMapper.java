package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.model.dto.UserResponse;
import com.edara.edara.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);
    UserResponse toResponse(User entity);
}
