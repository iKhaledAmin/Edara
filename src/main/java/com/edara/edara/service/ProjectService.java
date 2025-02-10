package com.edara.edara.service;


import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.MemberType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {
     ProjectResponse add(ProjectRequest projectRequest);


    /**
     * Adds a new member to a project. This method checks if the user is already a member of the project
     * and throws a {@code ConflictException} if the user is already assigned. If the request contains
     * employee details, the method adds the user as an employee with the provided salary details; otherwise,
     * it adds the user as a normal member.
     *
     * <p>
     * <b>Process:</b>
     * <ol>
     *     <li>Retrieve the project by its ID.</li>
     *     <li>Retrieve the user by their unique user code.</li>
     *     <li>Check if the user is already a member of the project and throw {@code ConflictException} if true.</li>
     *     <li>Retrieve the title if a title ID is provided.</li>
     *     <li>Determine whether to add the user as an employee member or a normal member based on the request ({@link MemberType}).</li>
     *     <li>Return the created member response.</li>
     * </ol>
     * </p>
     *
     * <p><b>Success Response:</b></p>
     * <pre>
     * {
     *   "member_id": 123,
     *   "member_code": "USR001",
     *   "member_name": "John Doe",
     *   "member_image": "base64EncodedImageString",
     *   "member_type": "EMPLOYEE",
     *   "member_title": "Software Engineer",
     *   "join_date": "2024-02-02",
     *   "employee_details": {
     *     "employee_type": "MONTHLY",
     *     "base_salary": 60000.00,
     *     "bonus_salary": 5000.00,
     *     "total_salary": 65000.00
     *   }
     * }
     * </pre>
     *
     * <p><b>Failure Scenarios:</b></p>
     * <ul>
     *     <li><b>ConflictException:</b> If the user is already a member of the project.</li>
     *     <li><b>NoSuchElementException:</b> If the project or user does not exist.</li>
     *     <li><b>IllegalArgumentException:</b> If required fields such as project ID, user code, member role, or member type are missing.</li>
     * </ul>
     *
     * @param memberRequest ({@link MemberRequest}) The request object containing the project ID, user code, member role, member type,
     *                     optional title ID, and optional employee details ({@link EmployeeRequest}).
     * @return {@code MemberResponse} containing details of the newly added member.
     * @throws ConflictException If the user is already assigned to the project.
     * @throws NoSuchElementException If the project or user is not found.
     * @throws IllegalArgumentException If required fields are missing in the request.
     */
    MemberResponse addMemberToProject(MemberRequest memberRequest);



    /**
     * Removes a member from a project.
     *
     * <p>
     * This method retrieves the  {@link User} and {@link Project} entities based on
     * the provided user code and project ID. It then verifies whether the member is still working
     * on a task by calling {@code throwExceptionIfMemberStillWorkingOnTask}. If the member is
     * actively working on a task, an exception is thrown, preventing their removal.
     * </p>
     *
     * <p>
     * If the member is not actively working on a task, the method then checks whether
     * they have an active attendance record for the project. If an ongoing attendance
     * entry exists, their attendance is properly ended before proceeding with the removal.
     * </p>
     *
     * <p>
     * and the project's member lists. The explicit deletion of the member entity
     * is unnecessary because the {@code orphanRemoval = true} setting in the entity
     * relationships ensures automatic removal.
     * </p>
     *
     *
     * @param userCode  The unique code identifying the user.
     * @param projectId The ID of the project from which the member should be removed.
     * @throws ConflictException if the member is still working on a task.
     */
    void deleteMemberFromProject(String userCode, Long projectId);
    MemberResponse updateMemberOfProject(MemberRequest memberRequest);

    MemberResponse getResponseMemberOfProjectByMemberId(Long memberI);
    List<MemberResponse> getResponseAllMembersByProjectId(Long projectId);
    MemberResponse getResponseMemberByMemberId(Long memberId);

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
