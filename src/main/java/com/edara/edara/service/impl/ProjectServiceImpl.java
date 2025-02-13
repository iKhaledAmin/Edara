package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.mapper.ProjectMapper;
import com.edara.edara.repository.ProjectRepo;
import com.edara.edara.service.*;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;
    private final MemberService memberService;
    private final UserServiceImpl userService;
    private final TitleService titleService;
    private final CurrentAttendanceService currentAttendanceService;
    private final DailyAttendanceService dailyAttendanceService;
    private final NonNullBeanUtils nonNullBeanUtils;




    @Transactional
    private void aggregateDailyAttendance(Long memberId, Long projectId) {
        dailyAttendanceService.aggregateDailyMemberAttendancesOfProject(memberId, projectId);
    }

    //@Scheduled(cron = "0 * * * * ?") // Runs every minute
    @Scheduled(cron = "0 0 * * * ?") // Runs at the start of every hour
    public void aggregateAllMemberDailyAttendancesInProject() {
        //System.out.println("Aggregating all member daily attendances in projects...");
        Integer currentHour = LocalDateTime.now().getHour();
        List<Project> projects = projectRepo.findAllByAggregationHour(currentHour);
        if (!projects.isEmpty()) {
            projects.forEach(project -> {
                // Fetch all members of the project
                List<Member> members = project.getMembers();
                if (!members.isEmpty()) {
                    // Perform aggregation
                    members.forEach(member -> {
                        aggregateDailyAttendance(member.getId(), project.getId());
                    });
                }

            });
        }

    }



    private Long getNextId(){
        Long lastId = projectRepo.getLastId();
        if(lastId == null)
            return 0L;
        else
            return ++lastId;
    }
    private String hashIdToSixDigit(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());
            BigInteger number = new BigInteger(1, encodedhash);
            BigInteger maxDigits = new BigInteger("1000000"); // 10^6
            BigInteger reducedNumber = number.mod(maxDigits);
            return String.format("%06d", reducedNumber); // Ensure it is 6 digits with leading zeros if necessary
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUniqueProjectCode() {
        Long nextId = getNextId();
        String sequenceNumber = hashIdToSixDigit(nextId.toString());
        return sequenceNumber;
    }

    @Override
    public ProjectResponse toResponse(Project project) {
        return projectMapper.toResponse(project);
    }

    @Override
    public Project toEntity(ProjectRequest projectRequest) {
        return projectMapper.toEntity(projectRequest);
    }

    private void throwExceptionIfUserHasProjectWithSameName(String projectName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUserName(authentication.getName());

        // Check if the user is the OWNER of a project with the same name
        boolean isOwnerOfSameNameProject = user.getMembers().stream()
                .anyMatch(memberShip ->
                        memberShip.getProject().getName().equalsIgnoreCase(projectName) &&
                                memberShip.getMemberRole().equals(MemberRole.OWNER)
                );

        // Throw an exception if the user is the OWNER of a project with the same name
        if (isOwnerOfSameNameProject) {
            throw new ConflictException("You already own a project with the same name.");
        }
    }

    @Override
    public Project create(ProjectRequest projectRequest) {

        throwExceptionIfUserHasProjectWithSameName(projectRequest.getName());

        Project newProject = toEntity(projectRequest);
        newProject.setCode(generateUniqueProjectCode());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUserName(authentication.getName());

        Member owner = memberService.add(user, newProject, MemberRole.OWNER, null);

        newProject.getMembers().add(owner);

        return newProject;
    }

    @Override
    public Project save(Project project) {
        return projectRepo.save(project);
    }

    @Override
    public ProjectResponse add(ProjectRequest projectRequest) {
        Project newProject = create(projectRequest);
        return toResponse(save(newProject));
    }

    @Override
    public Project updateEntity(Long projectId, Project newProject) {
        Project existedProject = getById(projectId);

        // If the new project name is different, ensure no other project exists with the same new name.
        if (!existedProject.getName().equalsIgnoreCase(newProject.getName())) {
            throwExceptionIfUserHasProjectWithSameName(newProject.getName());
        }

        // Copy properties from newProject to existedProject, excluding the "id", "code", "createdAt", "type"
        nonNullBeanUtils.copyProperties(newProject, existedProject, "id", "code", "createdAt", "type","memberShips", "tasks", "titles");

        // Save the updated user
        existedProject = projectRepo.save(existedProject);

        return existedProject;
    }

    @Override
    public ProjectResponse update(Long projectId, ProjectRequest projectRequest) {

        Project newProject = projectMapper.toEntity(projectRequest);

        Project existedProject = updateEntity(projectId,newProject);

        return projectMapper.toResponse(existedProject);
    }

    private void throwExceptionIfProjectStillHasEmployees(Project project) {
        // Check if the project still has users except the OWNER
        boolean hasEmployees = project.getMembers().stream()
                .anyMatch(memberShip -> !memberShip.getMemberRole().equals(MemberRole.OWNER));

        if (hasEmployees) {
            throw new ConflictException("Cannot delete the project.still has employees associated with this project.");
        }
    }
    @Override
    public void delete(Long projectId) {
        Project project = getById(projectId);
        throwExceptionIfProjectStillHasEmployees(project);
        projectRepo.deleteById(projectId);
    }

    @Override
    public Optional<Project> getEntityById(Long projectId) {
        return projectRepo.findById(projectId);
    }

    @Override
    public Project getById(Long projectId) {
        return getEntityById(projectId).orElseThrow(
                () -> new NoSuchElementException("There is no project with id  = " + projectId)
        );
    }

    @Override
    public ProjectResponse getResponseById(Long projectId) {
        return projectMapper.toResponse(getById(projectId));
    }

    @Override
    public List<ProjectResponse> getAll() {
        return projectRepo.findAll()
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

}
