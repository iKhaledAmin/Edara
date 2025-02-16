package com.edara.edara.member;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.project.Project;
import com.edara.edara.title.Title;
import com.edara.edara.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public interface MemberService {

    Member toEntity(MemberRequest request);

    MemberResponse toResponse(Member entity);


    /**
     * Adds a new member to a project.
     *
     * <p>
     * This method creates a new {@link Member} entity and assigns the given {@link User} to the specified
     * {@link Project} with a particular {@link MemberRole} and {@link Title}. The newly created member is
     * assigned a default membership type of {@code NORMAL_MEMBER}.
     * </p>
     *
     * <p>
     * The created member is also added to the project's list of members to maintain bidirectional consistency.
     * After creation, the member entity is saved to the database.
     * </p>
     *
     * <p>
     * <strong>Important:</strong>
     * This method does not validate whether the provided
     * {@link User} and {@link Project} correspond  before
     * attempting creation of the new member to the project. It assumes that the caller provides valid input.
     * </p>
     *
     * @param user       The user being added as a project member.
     * @param project    The project to which the user is being assigned.
     * @param memberRole The role of the member within the project.
     * @param title      The title associated with the member in the project.
     * @return The newly created {@link Member} entity.
     */
    Member add(User user, Project project, MemberRole memberRole, Title title);


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
    public MemberResponse add(MemberRequest memberRequest);

    Optional<Member> getEntityByUserIdAndProjectId(Long userId, Long projectId);
    Member getByUserIdAndProjectId(Long userId, Long projectId);

    Optional<Member> getEntityByUserCodeAndProjectId(String userCode, Long projectId);
    Member getByUserCodeAndProjectId(String userCode, Long projectId);




    /**
     * Updates an existing member's information in the system.
     *
     * <p>
     * This method updates the details of a {@link Member} entity based on the provided
     * {@link MemberRequest}. It first retrieves the existing member from the database using
     * the provided {@code memberId}. If the member exists, it then updates the member’s properties
     * with the values from the new {@link MemberRequest}, excluding sensitive or non-updatable
     * fields such as {@code id}, {@code memberType}, {@code user}, {@code project}, {@code tasks},
     * {@code currentAttendances}, {@code dailyAttendances}, and {@code employee}.
     * </p>
     *
     * <p>
     * If the existing member is of type {@code EMPLOYEE}, it updates the employee details
     * by calling the {@link EmployeeService#updateEntity} method to ensure the employee’s details
     * are properly updated without deleting the employee entity during the update process.
     * </p>
     *
     * <p>
     * After updating the member's details and the employee details (if applicable), the updated
     * member is saved to the database.
     * </p>
     *
     * @param memberId      The unique ID of the member to be updated.
     * @param memberRequest The new details for the member encapsulated in a {@link MemberRequest}.
     * @return A {@link MemberResponse} containing the updated details of the member.
     */
    MemberResponse update(Long memberId, MemberRequest memberRequest);


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
    void delete(String userCode, Long projectId);


    Optional<Member> getEntityById(Long memberId);
    Member getById(Long memberId);
    MemberResponse getResponseById(Long memberId);
    List<Member> getAllByProjectId(Long projectId);
    List<MemberResponse> getAllResponseByProjectId(Long projectId);


    boolean isExistsByTitleIdAndProjectId(Long titleId, Long projectId);
}
