package com.edara.edara.service;

import com.edara.edara.model.entity.Person;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface PersonService {

     //Person editProfile(Long personId, UserRequest personRequest);
     Optional<Person> getOptionalById(Long id);
     Person getById(Long personId);
     Optional<Person> getOptionalByAccount(String account);
     Person getByAccount(String userName);

}
