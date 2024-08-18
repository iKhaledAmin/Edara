package com.edara.edara.repository;

import com.edara.edara.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    boolean existsByPersonalCode(String personalCode);
}
