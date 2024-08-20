package com.edara.edara.service;

import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface PersonService {

     PersonResponse editProfile(Long personId, EditProfileRequest editProfileRequest);
     Optional<Person> getEntityById(Long id);
     Person getById(Long personId);
     Optional<Person> getEntityByUserName(String account);
     Person getByUserName(String userName);

}
