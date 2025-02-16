package com.edara.edara.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);
    User toEntity(RegistrationRequest request);
    UserResponse toResponse(User entity);


}
