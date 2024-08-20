package com.edara.edara.repository;

import com.edara.edara.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    @Query("SELECT MAX(u.id) FROM User u")
    Long getLastId();
    Optional<User> findByAccount(String account);

}
