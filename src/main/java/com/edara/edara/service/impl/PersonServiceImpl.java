package com.edara.edara.service.impl;


import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.entity.Person;
import com.edara.edara.model.mapper.PersonMapper;
import com.edara.edara.repository.PersonRepo;
import com.edara.edara.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepo personRepo;
    private final PersonMapper personMapper;
    private final PasswordEncoder passwordEncoder;


    private void copyProperties(Person source, Person target) {

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        if(!passwordEncoder.matches(source.getPassword(),target.getPassword()))
            target.setPassword(passwordEncoder.encode(source.getPassword()));
//        target.setPassword(source.getPassword());
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
    public Person editProfile(Long personId, EditProfileRequest editProfileRequest) {

        Person newPerson = personMapper.toEntity(editProfileRequest);
        newPerson = update(personId,newPerson);

        return newPerson;
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
    public Optional<Person> getEntityByUserName(String account) {
        return personRepo.findByUserName(account);
    }

    @Override
    public Person getByUserName(String account) {
        return getEntityByUserName(account).orElseThrow(
                () -> new NoSuchElementException("There is no person with userName = " + account)
        );
    }


}


