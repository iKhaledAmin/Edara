package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.EmployeeType;
import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.enums.MemberType;
import com.edara.edara.model.mapper.MemberMapper;
import com.edara.edara.repository.MemberRepo;
import com.edara.edara.service.EmployeeService;
import com.edara.edara.service.MemberService;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepo memberRepo;
    private final MemberMapper memberMapper;
    private final EmployeeService employeeService;
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

    @Override
    public Member add(User user, Project project, MemberRole memberRole, Title title) {
        return addNormalMember(user, project, memberRole, title);
    }
    @Override
    public Member add(User user, Project project, MemberRole memberRole,MemberType memberType, Title title, EmployeeType employeeType, Double baseSalary, Double bonusSalary) {
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
        if (memberId == null || newMember == null)
            return null;

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
        if (memberId == null || memberRequest == null)
            return null;

        Member newMember = toEntity(memberRequest);
        Member existedMember = updateEntity(memberId,newMember);

        return memberMapper.toResponse(existedMember);
    }


    @Transactional
    @Override
    public void delete(Long memberId) {
        if (memberId == null) return;
        Member member = getById(memberId);

        member.getProject().getMembers().remove(member);
        member.getUser().getMembers().remove(member);

        //memberRepo.deleteById(membershipId); // no need for this because orphanRemoval = true in the relation
                                                   // Member and (Project and User) .
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


}
