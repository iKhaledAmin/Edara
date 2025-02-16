package com.edara.edara.repository;

import com.edara.edara.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person,Long> {
    Optional<Person> findByAccount(String account);
}
