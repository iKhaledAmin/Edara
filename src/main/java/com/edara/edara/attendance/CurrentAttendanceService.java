package com.edara.edara.attendance;

import com.edara.edara.attendance.CurrentAttendanceResponse;
import com.edara.edara.attendance.CurrentAttendance;
import com.edara.edara.attendance.DailyAttendance;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CurrentAttendanceService {
    CurrentAttendance create();

    CurrentAttendance save(CurrentAttendance currentAttendance);

    CurrentAttendance recordCurrentAttendance(DailyAttendance dailyAttendance);

    CurrentAttendanceResponse toResponse(CurrentAttendance entity);

    //DailyAttendancesOfUserResponse toResponse(List<CurrentAttendance> attendances);

    Optional<CurrentAttendance> getEntityById(Long id);

    Optional<CurrentAttendance> getOnGoingAttendanceByMemberAndProject(Long memberId, Long projectId);

    public CurrentAttendance endCurrentAttendance(CurrentAttendance currentAttendance);


}