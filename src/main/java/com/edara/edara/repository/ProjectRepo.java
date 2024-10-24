package com.edara.edara.repository;

import com.edara.edara.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {
    @Query("SELECT MAX(p.id) FROM Project p")
    Long getLastId();
}
