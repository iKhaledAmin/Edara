package com.edara.edara.service;

import com.edara.edara.model.dto.CurrentAttendanceResponse;
import com.edara.edara.model.entity.CurrentAttendance;
import com.edara.edara.model.entity.DailyAttendance;
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