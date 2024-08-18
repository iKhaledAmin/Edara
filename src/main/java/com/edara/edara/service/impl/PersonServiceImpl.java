package com.edara.edara.service.impl;

import com.edara.edara.model.dto.PersonRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import com.edara.edara.model.mapper.PersonMapper;
import com.edara.edara.repository.PersonRepo;
import com.edara.edara.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepo personRepo;
    private final PersonMapper personMapper;

    @Override
    public PersonResponse editProfile(Long personId, PersonRequest personRequest) {
        Person existedPerson = getById(personId);
        Person updatedPerson = personMapper.toEntity(personRequest);

        // Copy properties from updatedPerson to existedPerson, excluding the id and other fields
        BeanUtils.copyProperties(updatedPerson, existedPerson, "id","account","role");

        existedPerson = personRepo.save(existedPerson);

        return personMapper.toResponse(existedPerson);
    }
    @Override
    public Optional<Person> getEntityById(Long id) {
        return personRepo.findById(id);
    }

    public Person getById(Long personId) {
        return getEntityById(personId).orElseThrow(
                () -> new NoSuchElementException("There is no person with id  = " + personId)
        );
    }
}
