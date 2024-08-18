package com.edara.edara.service;

import com.edara.edara.model.dto.PersonRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface PersonService {

    public PersonResponse editProfile(Long personId, PersonRequest personRequest);
    public Optional<Person> getEntityById(Long id);

}
