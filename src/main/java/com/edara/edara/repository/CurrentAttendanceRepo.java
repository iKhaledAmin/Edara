package com.edara.edara.repository;

import com.edara.edara.model.entity.CurrentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentAttendanceRepo extends JpaRepository<CurrentAttendance, Long> {

    Optional<CurrentAttendance> findByMemberIdAndProjectIdAndEndTimeIsNull(Long memberId, Long projectId);

    @Query("SELECT a FROM CurrentAttendance a WHERE a.member.id = :memberId " +
            "AND a.project.id = :projectId " +
            "AND FUNCTION('DAY', a.startTime) = FUNCTION('DAY', :day) " +
            "AND a.endTime IS NOT NULL AND a.isAggregated = false")
    List<CurrentAttendance> findUnAggregatedAttendancesByMemberAndProjectOnDay(Long memberId, Long projectId, LocalDateTime day);

}
