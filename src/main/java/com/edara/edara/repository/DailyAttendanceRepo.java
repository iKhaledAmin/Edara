package com.edara.edara.repository;

import com.edara.edara.model.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyAttendanceRepo extends JpaRepository<DailyAttendance, Long> {

    List<DailyAttendance> findAllByProjectIdAndDateAndIsAggregatedTrue(Long projectId, LocalDateTime date);

    List<DailyAttendance> findAllByProjectIdAndMemberIdAndIsAggregatedTrue(Long projectId, Long memberId);

    @Query("SELECT a FROM DailyAttendance a " +
            "WHERE a.project.id = :projectId " +
            "AND a.member.id = :memberId " +
            "AND a.isAggregated = true " +
            "AND MONTH(a.date) = :month " +
            "AND YEAR(a.date) = :year")
    List<DailyAttendance> findAllByProjectIdAndMemberIdInSpecificMonth(Long projectId, Long memberId, Integer year,Integer month);

    @Query("SELECT a FROM DailyAttendance a " +
            "WHERE a.project.id = :projectId " +
            "AND a.member.id = :memberId " +
            "AND a.isAggregated = true " +
            "AND YEAR(a.date) = :year")
    List<DailyAttendance> findAllByProjectIdAndMemberIdInSpecificYear(Long projectId, Long memberId, Integer year);

    @Query("SELECT a FROM DailyAttendance a " +
            "WHERE a.member.id = :memberId " +
            "AND a.project.id = :projectId " +
            "AND a.isAggregated = false " +
            "AND a.date = :day")
    Optional<DailyAttendance> findByMemberIdAndProjectIdAndDateIsAggregatedFalse(
            Long memberId, Long projectId, LocalDate day);

    List<DailyAttendance> findAllByProjectIdAndIsAggregatedTrue(Long projectId);

    Optional<DailyAttendance> findByMember_IdAndProject_IdAndDateAndIsAggregatedIsFalse(Long memberId, Long projectId,LocalDate date);    List<DailyAttendance> findAllByProjectIdAndEndTimeIsNullAndIsAggregatedFalse(Long projectId);

    List<DailyAttendance> findAllByProjectIdAndMemberIdAndStartTimeNullAndEndTimeIsNull(Long projectId, Long memberId);

    List<DailyAttendance> findAllByProjectIdAndDateAndStartTimeNullAndEndTimeIsNull(Long projectId, LocalDate date);
    List<DailyAttendance> findAllByProjectIdAndStartTimeNullAndEndTimeIsNull(Long projectId);

}


