package com.edara.edara.repository;

import com.edara.edara.model.entity.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberShipRepo extends JpaRepository<MemberShip, Long> {
    Optional<MemberShip> findByUserIdAndProjectId(Long userId, Long projectId);
}
