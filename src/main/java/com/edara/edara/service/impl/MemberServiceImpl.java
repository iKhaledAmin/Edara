package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.EmployeeRequest;
import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.*;
import com.edara.edara.model.enums.EmployeeType;
import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.enums.MemberType;
import com.edara.edara.model.enums.TaskStatus;
import com.edara.edara.model.mapper.MemberMapper;
import com.edara.edara.repository.MemberRepo;
import com.edara.edara.service.*;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepo memberRepo;
    private final MemberMapper memberMapper;
    private final EmployeeService employeeService;
    private final ServiceLocator serviceLocator;
    private final NonNullBeanUtils nonNullBeanUtils;

    @Override
    public Member toEntity(MemberRequest request) {
        return memberMapper.toEntity(request);
    }

    @Override
    public MemberResponse toResponse(Member entity) {
        return memberMapper.toResponse(entity);
    }

    private Member create(){
        Member newMember = new Member();
        newMember.setJoinDate(LocalDate.now());
        return newMember;
    }
    private Member save(Member member) {
        return memberRepo.save(member);
    }

    private Member addNormalMember(User user, Project project, MemberRole memberRole, Title title) {
        if (user == null || project == null || memberRole == null)
            return null;

        Member newMember = create();
        
        newMember.setUser(user);
        newMember.setProject(project);
        newMember.setMemberRole(memberRole);
        newMember.setTitle(title);
        newMember.setMemberType(MemberType.NORMAL_MEMBER);

        project.getMembers().add(newMember); // Ensure bidirectional consistency
        return save(newMember);
    }
    private Member addEmployeeMember(User user, Project project, MemberRole memberRole, Title title, EmployeeType employeeType, Double baseSalary, Double bonusSalary) {
        Member newMember = addNormalMember(user, project, memberRole, title);
        newMember = employeeService.add(newMember, employeeType, baseSalary, bonusSalary).getMember();
        newMember.setMemberType(MemberType.EMPLOYEE_MEMBER);

        return save(newMember); // Save updated Member with linked Employee
    }


    private Member addMember(User user, Project project, MemberRole memberRole,MemberType memberType, Title title, EmployeeType employeeType, Double baseSalary, Double bonusSalary) {
        Member newMember = (memberType == MemberType.EMPLOYEE_MEMBER)
                ? addEmployeeMember(
                user,
                project,
                memberRole,
                title,
                employeeType,
                baseSalary,
                bonusSalary
        )
                : addNormalMember(user, project, memberRole, title);

        return newMember;
    }


    private void throwExceptionIfMemberAlreadyExistsInProject(User user, Project project) {
        if (memberRepo.existsByUserAndProject(user, project)) {
            throw new ConflictException("Member already exists in the project.");
        }
    }
    @Override
    public Member add(User user, Project project, MemberRole memberRole, Title title) {
        return addNormalMember(user, project, memberRole, title);
    }

    @Transactional
    @Override
    public MemberResponse add(MemberRequest memberRequest) {
        Project project = serviceLocator.getService(ProjectService.class).getById(memberRequest.getProjectId());
        User user = serviceLocator.getService(UserService.class).getByCode(memberRequest.getUserCode());

        throwExceptionIfMemberAlreadyExistsInProject(user, project);

        Title title = (memberRequest.getTitleId() != null) ? serviceLocator.getService(TitleService.class).getById(memberRequest.getTitleId()) : null;
        EmployeeRequest employeeRequest = memberRequest.getEmployeeRequest();

        Member newMember = addMember(
                user,
                project,
                memberRequest.getMemberRole(),
                memberRequest.getMemberType(),
                title,
                (employeeRequest != null) ? employeeRequest.getType() : null,
                (employeeRequest != null) ? employeeRequest.getBaseSalary() : null,
                (employeeRequest != null) ? employeeRequest.getBonusSalary() : null
        );

        return toResponse(newMember);
    }



    @Override
    public Optional<Member> getEntityByUserIdAndProjectId(Long userId, Long projectId) {
        return memberRepo.findByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Member getByUserIdAndProjectId(Long userId, Long projectId) {
        return getEntityByUserIdAndProjectId(userId, projectId).orElseThrow(
                () -> new ConflictException("User with id = " + userId + " not involved in this project.")
        );
    }

    @Override
    public Optional<Member> getEntityByUserCodeAndProjectId(String userCode, Long projectId) {
        return memberRepo.findByUser_UserCodeAndProject_Id(userCode, projectId);
    }


    @Override
    public Member getByUserCodeAndProjectId(String userCode, Long projectId) {
        return getEntityByUserCodeAndProjectId(userCode, projectId).orElseThrow(
                () -> new ConflictException("User with code = " + userCode + " not involved in this project.")
        );
    }


    private Member updateEntity(Long memberId, Member newMember) {

        Member existedMember = getById(memberId);

        // Copy properties from newMember to existedMember, excluding the "id", "memberType", "user", "project", "tasks", "currentAttendances", "dailyAttendances, "employee"
        nonNullBeanUtils.copyProperties(newMember, existedMember, "id", "memberType", "user", "project", "tasks", "currentAttendances", "dailyAttendances", "employee");

        // Update employee details only if the member is an employee
        if (existedMember.getMemberType() == MemberType.EMPLOYEE_MEMBER && newMember.getEmployee() != null) {
            // Ensure that the employee is not deleted during update
            existedMember.setEmployee(
                    employeeService.updateEntity(existedMember.getEmployee().getId(), newMember.getEmployee())
            );
        }

        // Save the updated member
        existedMember = memberRepo.save(existedMember);

        return existedMember;
    }

    @Override
    public MemberResponse update(Long memberId, MemberRequest memberRequest) {

        serviceLocator.getService(ProjectService.class).getById(memberRequest.getProjectId());
        serviceLocator.getService(UserService.class).getByCode(memberRequest.getUserCode());
        getByUserCodeAndProjectId(memberRequest.getUserCode(), memberRequest.getProjectId());

        Member newMember = toEntity(memberRequest);
        Member existedMember = updateEntity(memberId,newMember);

        return memberMapper.toResponse(existedMember);
    }

    private void throwExceptionIfMemberStillWorkingOnTask(Member member) {
        if (member.getTasks().stream()
                .anyMatch(task -> task.getStatus().equals(TaskStatus.ON_WORKING))) {
            throw new ConflictException("Member is still working on a task.");
        }
    }


    @Transactional
    @Override
    public void delete(String userCode, Long projectId) {

        User user = serviceLocator.getService(UserService.class).getByCode(userCode);
        Project project = serviceLocator.getService(ProjectService.class).getById(projectId);
        Member member = getByUserCodeAndProjectId(userCode, projectId);

        throwExceptionIfMemberStillWorkingOnTask((member));

        // End the ongoing daily attendance
        Optional<DailyAttendance> onGoingDailyAttendance =
                serviceLocator.getService(DailyAttendanceService.class).getOnGoingDailyAttendanceByUserCodeAndProjectId(userCode, projectId);
        if (onGoingDailyAttendance .isPresent()) {
            serviceLocator.getService(DailyAttendanceService.class).endAttendance(userCode, projectId);
        }

        user.getMembers().remove(member);
        project.getMembers().remove(member);
        memberRepo.delete(member);

    }


    @Override
    public Optional<Member> getEntityById(Long memberId) {
        return memberRepo.findById(memberId);
    }
    @Override
    public Member getById(Long memberId) {
        return getEntityById(memberId).orElseThrow(
                () -> new NoSuchElementException("There is no member with id  = " + memberId)
        );
    }
    @Override
    public MemberResponse getResponseById(Long memberId) {
        return toResponse(getById(memberId));
    }

    @Override
    public List<Member> getAllByProjectId(Long projectId) {
        return memberRepo.findAllByProjectId(projectId);
    }

    @Override
    public List<MemberResponse> getAllResponseByProjectId(Long projectId) {
        return getAllByProjectId(projectId).stream()
                .map(memberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExistsByTitleIdAndProjectId(Long titleId, Long projectId) {
        return memberRepo.existsByTitleIdAndProjectId(titleId, projectId);
    }
}
