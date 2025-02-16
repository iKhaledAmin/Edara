package com.edara.edara.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task,Long> {
    @Query("SELECT MAX(t.id) FROM Task t")
    Long getLastId();

//    @Query("SELECT t FROM Task t JOIN t.member m WHERE m.user.id = :userId")
//    List<Task> findAllByUserId(Long userId);
//
//    @Query("SELECT t FROM Task t " +
//            "JOIN t.member m " +
//            "JOIN m.user u " +
//            "JOIN t.project p " +
//            "WHERE u.id = :userId AND p.id = :projectId")
//    List<Task> findAllByUserIdAndProjectId(Long userId, Long projectId);


    List<Task> findByMember_User_UserCode(String userCode);

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByMember_User_UserCodeAndProject_Id(String userCode, Long projectId);


}
