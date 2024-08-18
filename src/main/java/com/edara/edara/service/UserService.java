package com.edara.edara.service;

import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.model.dto.UserResponse;
import com.edara.edara.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends CrudService<UserRequest, User, UserResponse,Long> {
    UserResponse register(UserRequest userRequest);
     PersonResponse editProfile(Long userId, UserRequest userRequest);

}
