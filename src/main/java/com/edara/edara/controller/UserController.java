package com.edara.edara.controller;


import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest userRequest) {
            return new ResponseEntity<>(userService.register(userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/edit-profile/{userId}")
    public ResponseEntity<?> editProfile(@RequestBody @Valid EditProfileRequest editProfileRequest, @PathVariable Long userId){
            return new ResponseEntity<>(this.userService.editProfile(userId,editProfileRequest), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long userId){
            return new ResponseEntity<>(this.userService.getResponseById(userId),HttpStatus.OK);
    }
}
