package com.edara.edara.service;

import com.edara.edara.model.dto.CurrentAttendanceResponse;
import com.edara.edara.model.entity.CurrentAttendance;
import com.edara.edara.model.entity.DailyAttendance;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface CurrentAttendanceService {
    CurrentAttendance create();

    CurrentAttendance save(CurrentAttendance currentAttendance);

    /**
     * Creates and adds a new attendance record for the specified member and project.
     *
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Creates a new {@code CurrentAttendance} entity.</li>
     *   <li>Associates the attendance record with the provided member and project.</li>
     *   <li>Adds the attendance record to the member's and project's attendance collections.</li>
     *   <li>Ensures that a {@code DailyAttendance} entry exists for the member, project, and current day.
     *       If no such entry exists, it creates one using the {@code dailyAttendanceService}.</li>
     *   <li>returns the created attendance record.</li>
     * </ul>
     *
     * @param member the membership entity representing the member associated with the attendance
     * @param project the project entity associated with the attendance
     * @return the newly created and saved {@code CurrentAttendance} entity
     */
    CurrentAttendance recordCurrentAttendance(Member member, Project project, DailyAttendance dailyAttendance);

    CurrentAttendanceResponse toResponse(CurrentAttendance entity);

    //DailyAttendancesOfUserResponse toResponse(List<CurrentAttendance> attendances);

    Optional<CurrentAttendance> getEntityById(Long id);

    Optional<CurrentAttendance> getEntityByMemberIdAndProjectIdAndEndTimeIsNull(Long memberId, Long projectId);

    List<CurrentAttendance> getUnAggregatedAttendancesByMemberAndProjectOnDay(Long memberId, Long projectId, LocalDateTime day);

    public CurrentAttendance endCurrentAttendance(CurrentAttendance currentAttendance);


}