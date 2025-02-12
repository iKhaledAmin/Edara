package com.edara.edara.repository;

import com.edara.edara.model.entity.CurrentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentAttendanceRepo extends JpaRepository<CurrentAttendance, Long> {

    Optional<CurrentAttendance> findByDailyAttendanceMemberIdAndDailyAttendanceProjectIdAndEndTimeIsNull(Long memberId, Long projectId);

    List<CurrentAttendance> findAllByDailyAttendanceMemberIdAndDailyAttendanceProjectIdAndIsAggregatedFalse(Long memberId, Long projectId);

}
