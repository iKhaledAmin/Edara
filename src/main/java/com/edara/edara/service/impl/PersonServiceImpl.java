package com.edara.edara.service.impl;


import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import com.edara.edara.model.mapper.PersonMapper;
import com.edara.edara.repository.PersonRepo;
import com.edara.edara.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepo personRepo;
    private final PersonMapper personMapper;


    private void copyProperties(Person source, Person target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPassword(source.getPassword());
        target.setEmail(source.getEmail());
        target.setBirthday(source.getBirthday());
        target.setImage(source.getImage());
        target.setGender(source.getGender());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setCountry(source.getCountry());
        target.setCity(source.getCity());

    }

    public Person update(Long personId, Person newPerson) {

        Person existedPerson = getById(personId);

        newPerson.setId(existedPerson.getId());
        newPerson.setRole(existedPerson.getRole());
        copyProperties(newPerson,existedPerson);

        return personRepo.save(existedPerson);
    }

    @Override
    public PersonResponse editProfile(Long personId, EditProfileRequest editProfileRequest) {

        Person newPerson = personMapper.toEntity(editProfileRequest);
        newPerson = update(personId,newPerson);

        return personMapper.toResponse(newPerson);
    }
    @Override
    public Optional<Person> getEntityById(Long id) {
        return personRepo.findById(id);
    }

    @Override
    public Person getById(Long personId) {
        return getEntityById(personId).orElseThrow(
                () -> new NoSuchElementException("There is no person with id  = " + personId)
        );
    }

    @Override
    public Optional<Person> getEntityByAccount(String account) {
        return personRepo.findByAccount(account);
    }

    @Override
    public Person getByAccount(String account) {
        return getEntityByAccount(account).orElseThrow(
                () -> new NoSuchElementException("There is no person with account = " + account)
        );
    }


}


