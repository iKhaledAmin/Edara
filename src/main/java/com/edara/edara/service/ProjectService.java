package com.edara.edara.service;


import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.Project;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {
     ProjectResponse add(ProjectRequest projectRequest);

    CurrentAttendanceResponse recordMemberAttendance(String employeeCode, Long projectId);
    CurrentAttendanceResponse endMemberAttendance(String employeeCode, Long projectId);
    List<DailyAttendanceResponse> getAllDailyAttendancesByProjectIdAndUserCode(Long projectId, String userCode, Integer year, Integer month);
    List<DailyAttendanceResponse> getAllDailyAttendancesByProjectId(Long projectId, LocalDateTime date);
    List<DailyAttendanceResponse> getAllCurrentAttendancesByProjectId(Long projectId);

    List<DailyAttendanceResponse> getAllAbsencesByProjectIdAndUserCode(Long projectId, String userCode);
    List<DailyAttendanceResponse> getAllAbsencesByProjectId(Long projectId, LocalDate date);



     TitleResponse addTitleToProject(TitleRequest titleRequest, Long projectId);
     void deleteTitleFromProject(Long titleId);
     List<TitleResponse> getResponseAllTitlesByProjectId(Long projectId);
}
