package com.edara.edara.service;

import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.EmployeeType;
import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.enums.MemberType;
import org.springframework.stereotype.Service;

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
     * Adds a new member to a project, supporting both employee and normal member types.
     *
     * <p>
     * This method determines whether the new member should be an {@code EMPLOYEE} or a {@code NORMAL_MEMBER}
     * based on the provided {@link MemberType}. If the member type is {@code NORMAL_MEMBER}, a new member
     * is created with the given role and title and assigned to the project.
     * </p>
     *
     * <p>
     * If the member type is {@code EMPLOYEE}, additional employee-related details such as
     * {@code employeeType}, {@code baseSalary}, and {@code bonusSalary} are set, and the employee
     * is linked to the project accordingly.
     * </p>
     *
     * <p>
     * The created member is added to the project’s list of members to ensure bidirectional consistency.
     * Once the member is created, it is saved to the database.
     * </p>
     *
     * <p>
     * <strong>Important:</strong>
     * This method does not validate whether the provided
     * {@link User} , {@link Project} , {@link MemberRole} , {@link MemberType} , {@link Title} and {@link EmployeeType} correspond  before
     * attempting creation of the new member to the project. It assumes that the caller provides valid input.
     * </p>
     *
     * @param user         The user being added as a project member.
     * @param project      The project to which the user is being assigned.
     * @param memberRole   The role of the member within the project.
     * @param memberType   The type of member, either {@code EMPLOYEE} or {@code NORMAL_MEMBER}.
     * @param title        The title associated with the member in the project.
     * @param employeeType The type of employee (only required if {@code memberType} is {@code EMPLOYEE}).
     * @param baseSalary   The base salary of the employee (only required if {@code memberType} is {@code EMPLOYEE}).
     * @param bonusSalary  The bonus salary of the employee (only required if {@code memberType} is {@code EMPLOYEE}).
     * @return The newly created {@link Member} entity.
     */
    Member add(User user, Project project, MemberRole memberRole, MemberType memberType, Title title, EmployeeType employeeType, Double baseSalary, Double bonusSalary);


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
    void delete(Long memberId);


    Optional<Member> getEntityById(Long memberId);
    public Member getById(Long memberId);
    public MemberResponse getResponseById(Long memberId);






}
