package com.edara.edara.service.impl;

import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.model.dto.UserResponse;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.Role;
import com.edara.edara.model.mapper.UserMapper;
import com.edara.edara.repository.UserRepo;
import com.edara.edara.service.PersonService;
import com.edara.edara.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final PasswordEncoder passwordEncoder;


    private void throwExceptionIfUserNameAlreadyExist(String account) {
        getEntityByUserName(account)
                .ifPresent(user -> { throw new RuntimeException("This user name is already exist.");
                });
    }

    private Long getNextId(){
        Long lastId = userRepo.getLastId();
        if(lastId == null)
            return 0L;
        else
            return ++lastId;
    }
    private String hashIdToSixDigit(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());
            BigInteger number = new BigInteger(1, encodedhash);
            BigInteger maxDigits = new BigInteger("1000000"); // 10^6
            BigInteger reducedNumber = number.mod(maxDigits);
            return String.format("%06d", reducedNumber); // Ensure it is 6 digits with leading zeros if necessary
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUniquePersonalCode(String firstName, String lastName) {
        String prefix = firstName.substring(0, 1).toLowerCase() + lastName.substring(0, 1).toLowerCase();
        Long nextId = getNextId();
        String sequenceNumber = hashIdToSixDigit(nextId.toString());
        return prefix + sequenceNumber;
    }

    @Override
    public UserResponse add(UserRequest userRequest) {
        throwExceptionIfUserNameAlreadyExist(userRequest.getUserName());
        User newUser = userMapper.toEntity(userRequest);
        newUser.setRole(Role.USER);
        newUser.setDateOfJoining(new Date());
        newUser.setPersonalCode(
                generateUniquePersonalCode(newUser.getFirstName(),newUser.getLastName())
        );

        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

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
    public UserResponse editProfile(Long userId, EditProfileRequest editProfileRequest){
        User user = (User) personService.editProfile(userId,editProfileRequest);

        user.setProfession(editProfileRequest.getProfession());
        user = updateEntity(userId,user);

        return userMapper.toResponse(user);
    }

    @Override
    public Optional<User> getEntityByUserName(String userName) {
        return userRepo.findByUserName(userName);
    }

    @Override
    public User getByUserName(String userName) {
        return getEntityByUserName(userName).orElseThrow(
                () -> new NoSuchElementException("There is no user with userName = " + userName)
        );
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
