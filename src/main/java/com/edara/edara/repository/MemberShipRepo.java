package com.edara.edara.repository;

import com.edara.edara.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberShipRepo extends JpaRepository<Member, Long> {
    Optional<Member> findByUserIdAndProjectId(Long userId, Long projectId);
    Optional<Member> findByUser_UserCodeAndProject_Id(String userCode, Long projectId);

}
