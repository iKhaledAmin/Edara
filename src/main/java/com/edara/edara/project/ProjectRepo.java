package com.edara.edara.project;

import com.edara.edara.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {
    @Query("SELECT MAX(p.id) FROM Project p")
    Long getLastId();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members")
    List<Project> findAllWithMemberShips();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.aggregationHour = :aggregationHour")
    List<Project> findAllByAggregationHour(Integer aggregationHour);
}
