package com.edara.edara.security;

import com.edara.edara.model.entity.Person;
import com.edara.edara.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonService personService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personService.getEntityByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException("There is no user with user name = " + username)
        );

        return CustomUserDetails.builder()
                .id(person.getId())
                .userName(person.getUserName())
                .role(person.getRole())
                .password(person.getPassword())
                .build();

    }
}
