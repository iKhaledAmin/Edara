package com.edara.edara.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService  {

    UserResponse toResponse(User user);
    User toEntity(UserRequest userRequest);
    User toEntity(RegistrationRequest request);

    User add(User newUser);
    User add(RegistrationRequest registrationRequest);
    UserResponse register(RegistrationRequest registrationRequest);

    User update(Long userId, User newUser);
    User update(Long userId , UserRequest userRequest);
    UserResponse editProfile(Long userId, UserRequest userRequest);


    void delete(Long userId);

    Optional<User> getOptionalById(Long userId);
     User getById(Long userId);
     UserResponse getResponseById(Long userId);


    Optional<User> getOptionalByAccount(String account);
    User getByAccount(String account);


    Optional<User> getOptionalByCode(String userCode);
    User getByCode(String userCode);
}
