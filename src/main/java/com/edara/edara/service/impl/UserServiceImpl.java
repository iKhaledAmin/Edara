package com.edara.edara.service.impl;

import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.model.dto.UserResponse;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.Role;
import com.edara.edara.model.mapper.UserMapper;
import com.edara.edara.repository.UserRepo;
import com.edara.edara.service.PersonService;
import com.edara.edara.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PersonService personService;
    //private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;


    private String generateUniquePersonalCode(String firstName, String lastName) {
        Long sequenceValue = (Long) entityManager.createNativeQuery("SELECT user_personal_code_seq.NEXTVAL FROM DUAL").getSingleResult();
        String randomDigits = String.format("%05d", sequenceValue % 100000);
        String initials = (firstName.charAt(0) + String.valueOf(lastName.charAt(0))).toLowerCase();
        return initials + randomDigits;
    }
    @Override
    public UserResponse add(UserRequest userRequest) {
        User newUser = userMapper.toEntity(userRequest);
        newUser.setRole(Role.USER);
        newUser.setDateOfJoining(new Date());
        newUser.setPersonalCode(
                generateUniquePersonalCode(newUser.getFirstName(),newUser.getLastName())
        );
//        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
//        newUser.setPassword(hashedPassword);
        newUser = userRepo.save(newUser);

        return userMapper.toResponse(newUser);
    }

    @Override
    public UserResponse register(UserRequest userRequest){
        return add(userRequest);
    }

    @Override
    public User updateEntity(Long userId, User newUser) {
        User existedUser = getById(userId);

        // Copy properties from newUser to existedUser, excluding the "id", "personalCode", "dateOfJoining", "role"
        BeanUtils.copyProperties(newUser, existedUser, "id", "personalCode", "dateOfJoining", "role");

        // Save the updated user
        existedUser = userRepo.save(existedUser);

        return existedUser;
    }

    @Override
    public UserResponse update(Long userId , UserRequest userRequest) {
        User newUser = userMapper.toEntity(userRequest);

        User existedUser = updateEntity(userId,newUser);

        return userMapper.toResponse(existedUser);
    }

    @Override
    public PersonResponse editProfile(Long userId,UserRequest userRequest){
        return personService.editProfile(userId,userRequest);
    }

    @Override
    public void delete(Long userId) {
        getById(userId);
        userRepo.deleteById(userId);
    }

    @Override
    public Optional<User> getEntityById(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public User getById(Long userId) {
        return getEntityById(userId).orElseThrow(
                () -> new NoSuchElementException("There is no user with id  = " + userId)
        );
    }

    @Override
    public UserResponse getResponseById(Long userId) {
        return userMapper.toResponse(getById(userId));
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
