package com.edara.edara.repository;

import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    Optional<Member> findByUserIdAndProjectId(Long userId, Long projectId);
    Optional<Member> findByUser_UserCodeAndProject_Id(String userCode, Long projectId);

    boolean existsByUserAndProject(User user, Project project);

    List<Member> findAllByProjectId(Long projectId);
}
