package com.edara.edara.security;

import com.edara.edara.model.entity.User;
import com.edara.edara.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.getEntityByUserName(username);
        user.orElseThrow((() -> new UsernameNotFoundException("User not found.")));

        if (user.isPresent()) {
            User existingUser = user.get();
            return CustomUserDetails.builder()
                    .id(existingUser.getId())
                    .userName(existingUser.getUserName())
                    .role(existingUser.getRole())
                    .password(existingUser.getPassword())
                    .build();
        }
        return null;
    }
}
