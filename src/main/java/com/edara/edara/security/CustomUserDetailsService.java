package com.edara.edara.security;

import com.edara.edara.model.entity.Person;
import com.edara.edara.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    PersonService personService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personService.getEntityByUserName(username);
        person.orElseThrow((() -> new UsernameNotFoundException("User not found.")));

        if (person.isPresent()) {
            Person existingPerson = person.get();

            return CustomUserDetails.builder()
                    .id(existingPerson.getId())
                    .userName(existingPerson.getUserName())
                    .role(existingPerson.getRole())
                    .password(existingPerson.getPassword())
                    .build();
        }
        return null;
    }
}
