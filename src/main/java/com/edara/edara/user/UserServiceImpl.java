package com.edara.edara.user;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.person.Role;
import com.edara.edara.global.utils.NonNullBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NonNullBeanUtils nonNullBeanUtils;




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

    private String generateUniqueCode() {
        Long nextId = getNextId();
        String sequenceNumber = hashIdToSixDigit(nextId.toString());
        return sequenceNumber;
    }


    @Override
    public UserResponse toResponse(User user) {
        return userMapper.toResponse(user);
    }

    @Override
    public User toEntity(UserRequest userRequest) {
        return userMapper.toEntity(userRequest);
    }
    @Override
    public User toEntity(RegistrationRequest request) {
        return userMapper.toEntity(request);
    }

    private User create(){

        User newUser = new User();
        newUser.setRole(Role.USER);
        newUser.setDateOfJoining(LocalDate.now());
        newUser.setUserCode(generateUniqueCode());

        return newUser;
    }

    private void throwExceptionIfAccountAlreadyExist(String account) {
        getOptionalByAccount(account)
                .ifPresent(user -> { throw new ConflictException("This account is already exist.");
                });
    }

//    private User create(RegistrationRequest registrationRequest) {
//        throwExceptionIfAccountAlreadyExist(registrationRequest.getAccount());
//        User newUser = toEntity(registrationRequest);
//        newUser.setRole(Role.USER);
//        newUser.setDateOfJoining(LocalDate.now());
//        newUser.setUserCode(generateUniqueCode());
//
//        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());
//        newUser.setPassword(hashedPassword);
//
//        return newUser;
//    }

    private User save(User user) {
        return userRepo.save(user);
    }


    public User add(User newUser) {
        throwExceptionIfAccountAlreadyExist(newUser.getAccount());
        newUser.setRole(Role.USER);
        newUser.setDateOfJoining(LocalDate.now());
        newUser.setUserCode(generateUniqueCode());

        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        return save(newUser);
    }

    public User add(RegistrationRequest registrationRequest) {
        User newUser = toEntity(registrationRequest);
        return add(newUser);
    }

    @Override
    public UserResponse register(RegistrationRequest registrationRequest){
        return toResponse(
                add(registrationRequest)
        );
    }

    @Override
    public User update(Long userId, User newUser) {
        User existingUser = getById(userId);

        // Check if the new password is provided and different from the existing one
        if (StringUtils.hasText(newUser.getPassword())
                && !passwordEncoder.matches(newUser.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        // Copy properties from newUser to existingUser, excluding sensitive fields
        nonNullBeanUtils.copyProperties(newUser, existingUser,
                "id", "userCode", "account", "password", "dateOfJoining", "role", "members");

        // Save the updated user
        return save(existingUser);
    }

    @Override
    public User update(Long userId , UserRequest userRequest) {
        User newUser = userMapper.toEntity(userRequest);
        return update(userId,newUser);
    }

    @Override
    public UserResponse editProfile(Long userId, UserRequest userRequest){
        User user = update(userId, userRequest);
        return toResponse(user);
    }

    @Override
    public Optional<User> getOptionalByAccount(String account) {
        return userRepo.findByAccount(account);
    }

    @Override
    public User getByAccount(String account) {
        return getOptionalByAccount(account).orElseThrow(
                () -> new NoSuchElementException("There is no user with account = " + account)
        );
    }

    @Override
    public Optional<User> getOptionalByCode(String userCode) {
        return userRepo.findByUserCode(userCode);
    }

    @Override
    public User getByCode(String userCode) {
        return getOptionalByCode(userCode).orElseThrow(
                () -> new NoSuchElementException("There is no user with userCode = " + userCode)
        );
    }

    @Override
    public void delete(Long userId) {
        getById(userId);
        userRepo.deleteById(userId);
    }

    @Override
    public Optional<User> getOptionalById(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public User getById(Long userId) {
        return getOptionalById(userId).orElseThrow(
                () -> new NoSuchElementException("There is no user with id  = " + userId)
        );
    }

    @Override
    public UserResponse getResponseById(Long userId) {
        return userMapper.toResponse(getById(userId));
    }

}
