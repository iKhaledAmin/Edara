package com.edara.edara.security;

import com.edara.edara.model.entity.User;
import com.edara.edara.model.mapper.UserMapper;
import com.edara.edara.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


//@CrossOrigin("*")
@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;


//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            return new ResponseEntity<>(userDetails, HttpStatus.ACCEPTED);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
//        }
//    }


    //@RequestMapping("/user")
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetailsAfterLogin(@RequestBody @Valid LoginRequest request) {
        User user = userService.getByUserName(request.getUserName());
        return new ResponseEntity<>(userMapper.toResponse(user), HttpStatus.CREATED);
    }
}