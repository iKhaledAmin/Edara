package com.edara.edara.repository;

import com.edara.edara.model.entity.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShipRepo extends JpaRepository<MemberShip, Long> {
    MemberShip findByUserIdAndProjectId(Long userId, Long projectId);
}
