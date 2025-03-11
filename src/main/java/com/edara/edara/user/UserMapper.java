package com.edara.edara.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "members", expression = "java(new java.util.ArrayList<>())")
    User toEntity(UserRequest request);

    @Mapping(target = "members", expression = "java(new java.util.ArrayList<>())")
    User toEntity(RegistrationRequest request);
    UserResponse toResponse(User entity);

}
