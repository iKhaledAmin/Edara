package com.edara.edara.attendance;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@AllArgsConstructor
public class AttendanceController {
    private final DailyAttendanceService dailyAttendanceService;


    @PostMapping("/record-attendance/{userCode}/{projectId}")
    public ResponseEntity<?> recordMemberAttendance(@PathVariable String userCode, @PathVariable Long projectId) {

        CurrentAttendanceResponse response = dailyAttendanceService.recordAttendance(userCode, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/end-attendance/{userCode}/{projectId}")
    public ResponseEntity<?> endMemberAttendance(@PathVariable String userCode, @PathVariable Long projectId) {

        CurrentAttendanceResponse response = dailyAttendanceService.endAttendance(userCode, projectId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    @GetMapping("/get-daily-attendances/{projectId}/{userCode}")
    ResponseEntity<?> getAllDailyAttendancesByProjectIdAndUserCode(@PathVariable Long projectId,
                                                                 @PathVariable String userCode,
                                                                 @RequestParam(required = false) Integer year,
                                                                 @RequestParam(required = false) Integer month) {
        List<DailyAttendanceResponse> responses =
                dailyAttendanceService.getResponseAllByProjectIdAndUserCode(projectId, userCode, year, month);

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/get-daily-attendances/{projectId}")
    ResponseEntity<?> getAllDailyAttendancesByProjectId(@PathVariable Long projectId,
                                                        @RequestParam(required = false) LocalDateTime date) {
        List<DailyAttendanceResponse> responses =
                dailyAttendanceService.getResponseAllByProjectId(projectId, date);

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/get-active-attendances/{projectId}")
    ResponseEntity<?> getAllActivetAttendancesByProjectId(@PathVariable Long projectId) {
        List<DailyAttendanceResponse> responses =
                dailyAttendanceService.getResponseAllActiveAttendancesByProjectId(projectId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/get-absences/{projectId}/{userCode}")
    ResponseEntity<?> getAllAbsencesByProjectIdAndUserCode(@PathVariable Long projectId,
                                                         @PathVariable String userCode) {
        List<DailyAttendanceResponse> responses =
                dailyAttendanceService.getResponseAllAbsencesByProjectIdAndUserCode(projectId, userCode);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/get-absences/{projectId}")
    ResponseEntity<?> getAllAbsencesByProjectId(@PathVariable Long projectId,
                                                @RequestParam(required = false) LocalDate date) {
        List<DailyAttendanceResponse> responses =
                dailyAttendanceService.getResponseAllAbsencesByProjectId(projectId, date);

        return ResponseEntity.ok(responses);
    }


}
