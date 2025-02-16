package com.edara.edara.user;


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
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
            return new ResponseEntity<>(userService.register(registrationRequest), HttpStatus.CREATED);
    }

    @PutMapping("/edit-profile/{userId}")
    public ResponseEntity<?> editProfile(@PathVariable Long userId, @RequestBody @Valid UserRequest userRequest){
            return new ResponseEntity<>(this.userService.editProfile(userId, userRequest), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId){
            return new ResponseEntity<>(this.userService.getResponseById(userId),HttpStatus.OK);
    }
}
