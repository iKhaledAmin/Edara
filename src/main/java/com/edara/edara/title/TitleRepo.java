package com.edara.edara.title;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitleRepo extends JpaRepository<Title, Long> {
    boolean existsByNameIgnoreCaseAndProjectId(String titleName, Long projectId);
    List<Title> findAllByProjectId(Long projectId);


}
