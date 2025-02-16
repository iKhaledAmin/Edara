package com.edara.edara.service.impl;


import com.edara.edara.model.entity.Person;
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

    @Override
    public Optional<Person> getOptionalById(Long id) {
        return personRepo.findById(id);
    }
    @Override
    public Person getById(Long personId) {
        return getOptionalById(personId).orElseThrow(
                () -> new NoSuchElementException("There is no person with id  = " + personId)
        );
    }
    @Override
    public Optional<Person> getOptionalByAccount(String account) {
        return personRepo.findByAccount(account);
    }
    @Override
    public Person getByAccount(String account) {
        return getOptionalByAccount(account).orElseThrow(
                () -> new NoSuchElementException("There is no person with userName = " + account)
        );
    }


}


