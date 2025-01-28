package com.edara.edara.controller;

import com.edara.edara.model.dto.*;
import com.edara.edara.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody @Valid ProjectRequest projectRequest) {
        return new ResponseEntity<>(projectService.add(projectRequest), HttpStatus.CREATED);
    }

    @PutMapping("/update/{projectId}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectRequest projectRequest, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.update(projectId,projectRequest), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-by-id/{projectId}")
    public ResponseEntity<?> getManagerById(@PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseById(projectId),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteById(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<?> addEmployeeToProject(@RequestBody @Valid MemberRequest memberRequest) {
        return new ResponseEntity<>(projectService.addEmployeeToProject(memberRequest), HttpStatus.CREATED);
    }
    @DeleteMapping("/delete-employee/{employeeId}/{projectId}")
    public ResponseEntity<?> deleteEmployeeFromProject(@PathVariable Long employeeId, @PathVariable Long projectId) {
        projectService.deleteEmployeeFromProject(employeeId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get-all-employees/{projectId}")
    ResponseEntity<?> getAllEmployeesByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseAllEmployeesByProjectId(projectId), HttpStatus.OK);
    }


    @PostMapping("/record-attendance/{userCode}/{projectId}")
    public ResponseEntity<?> recordMemberAttendance(@PathVariable String userCode, @PathVariable Long projectId) {

        CurrentAttendanceResponse response = projectService.recordMemberAttendance(userCode, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/end-attendance/{userCode}/{projectId}")
    public ResponseEntity<?> endMemberAttendance(@PathVariable String userCode, @PathVariable Long projectId) {

        CurrentAttendanceResponse response = projectService.endMemberAttendance(userCode, projectId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    @GetMapping("/get-all-daily-attendances/{projectId}/{userCode}")
    ResponseEntity<?> getAllDailyAttendancesByProjectIdAndUserId(@PathVariable Long projectId,
                                                                   @PathVariable String userCode,
                                                                   @RequestParam(required = false) Integer year,
                                                                   @RequestParam(required = false) Integer month) {
        List<DailyAttendanceResponse> responses =
                projectService.getAllDailyAttendancesByProjectIdAndUserCode(projectId, userCode, year, month);

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/get-all-daily-attendances/{projectId}")
    ResponseEntity<?> getAllDailyAttendancesByProjectId(@PathVariable Long projectId,
                                                                   @RequestParam(required = false) LocalDateTime date) {
        List<DailyAttendanceResponse> responses =
                projectService.getAllDailyAttendancesByProjectId(projectId, date);

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/get-all-daily-current-attendances/{projectId}")
    ResponseEntity<?> getAllDailyCurrentAttendancesByProjectId(@PathVariable Long projectId) {
        List<DailyAttendanceResponse> responses =
                projectService.getAllCurrentAttendancesByProjectId(projectId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/get-all-absences/{projectId}/{userCode}")
    ResponseEntity<?> getAllAbsencesByProjectIdAndUserId(@PathVariable Long projectId,
                                                         @PathVariable String userCode) {
        List<DailyAttendanceResponse> responses =
                projectService.getAllAbsencesByProjectIdAndUserCode(projectId, userCode);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/get-all-absences/{projectId}")
    ResponseEntity<?> getAllAbsencesByProjectId(@PathVariable Long projectId,
                                                @RequestParam(required = false) LocalDate date) {
        List<DailyAttendanceResponse> responses =
                projectService.getAllAbsencesByProjectId(projectId, date);

        return ResponseEntity.ok(responses);
    }


    @PostMapping("/add-task/{projectId}")
    public ResponseEntity<?> addTaskToProject(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.addTaskToProject(taskRequest,projectId), HttpStatus.CREATED);
    }
    @DeleteMapping("/delete-task/{taskId}")
    public ResponseEntity<?> deleteTaskFromProject(@PathVariable Long taskId) {
        projectService.deleteTaskFromProject(taskId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.ACCEPTED);
    }
    @PutMapping("/assign-task/{taskId}/{employeeId}")
    public ResponseEntity<?> assignTaskToEmployee(@PathVariable Long taskId, @PathVariable Long employeeId) {
        return new ResponseEntity<>( projectService.assignTaskToMember(taskId,employeeId), HttpStatus.ACCEPTED);
    }
    @GetMapping("/get-all-project-tasks/{projectId}")
    ResponseEntity<?> getAllTasksByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseAllTasksByProjectId(projectId), HttpStatus.OK);
    }
    @GetMapping("/get-all-user-tasks/{userId}")
    ResponseEntity<?> getAllTasksByUserId( @PathVariable Long userId){
        return new ResponseEntity<>(this.projectService.getResponseAllTasksByUserId(userId), HttpStatus.OK);
    }


    @PostMapping("/add-title/{projectId}")
    public ResponseEntity<?> addTitleToProject(@RequestBody @Valid TitleRequest titleRequest, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.addTitleToProject(titleRequest,projectId), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-title/{titleId}")
    public ResponseEntity<?> deleteTitleFromProject(@PathVariable Long titleId) {
        projectService.deleteTitleFromProject(titleId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all-project-titles/{projectId}")
    ResponseEntity<?> getAllTitlesByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseAllTitlesByProjectId(projectId), HttpStatus.OK);
    }
}
