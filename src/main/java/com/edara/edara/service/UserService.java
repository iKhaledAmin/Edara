package com.edara.edara.service;

import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends CrudService<UserRequest, User, UserResponse,Long> {
    UserResponse register(UserRequest userRequest);
    PersonResponse editProfile(Long userId, EditProfileRequest editProfileRequest);

    Optional<User> getEntityByUserName(String account);
    User getByUserName(String account);


}
