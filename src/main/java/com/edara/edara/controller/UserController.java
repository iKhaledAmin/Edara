package com.edara.edara.controller;


import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.UserRequest;
import com.edara.edara.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest userRequest) {
        try {
            return new ResponseEntity<>(userService.register(userRequest), HttpStatus.CREATED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }
    }

    @PutMapping("/edit-profile/{userId}")
    public ResponseEntity<?> editProfile(@RequestBody @Valid EditProfileRequest editProfileRequest, @PathVariable Long userId){
        try {
            return new ResponseEntity<>(this.userService.editProfile(userId,editProfileRequest), HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long userId){
        try {
            return new ResponseEntity<>(this.userService.getResponseById(userId),HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }
    }
}
