package com.edara.edara.repository;

import com.edara.edara.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task,Long> {
    @Query("SELECT MAX(t.id) FROM Task t")
    Long getLastId();
}
