package com.edara.edara.service.impl;

import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Task;
import com.edara.edara.model.entity.User;
import com.edara.edara.model.enums.ProjectRole;
import com.edara.edara.model.mapper.ProjectMapper;
import com.edara.edara.repository.ProjectRepo;
import com.edara.edara.service.MemberShipService;
import com.edara.edara.service.ProjectService;
import com.edara.edara.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;
    private final TaskService taskService;
    private final MemberShipService memberShipService;
    private final UserServiceImpl userService;



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

    private void throwExceptionIfUserHasProjectWithSameName(String projectName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUserName(authentication.getName());

        // Check if the user is the OWNER of a project with the same name
        boolean isOwnerOfSameNameProject = user.getMemberShips().stream()
                .anyMatch(memberShip ->
                        memberShip.getProject().getName().equalsIgnoreCase(projectName) &&
                                memberShip.getProjectRole().equals(ProjectRole.OWNER)
                );

        // Throw an exception if the user is the OWNER of a project with the same name
        if (isOwnerOfSameNameProject) {
            throw new IllegalStateException("You already own a project with the same name.");
        }
    }
    @Override
    public ProjectResponse add(ProjectRequest projectRequest) {
        throwExceptionIfUserHasProjectWithSameName(projectRequest.getName());

        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setCode(generateUniqueProjectCode());
        newProject.setCreatedAt(new Date());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUserName(authentication.getName());

        MemberShip memberShip = addUserToProject (user, newProject, ProjectRole.OWNER);
        newProject.getMemberShips().add(memberShip);

        projectRepo.save(newProject);

        return projectMapper.toResponse(newProject);
    }

    @Override
    public Project updateEntity(Long projectId, Project newProject) {
        Project existedProject = getById(projectId);

        if (!existedProject.getName().equalsIgnoreCase(newProject.getName())) {
            throwExceptionIfProjectStillHasEmployees(existedProject);
        }

        // Copy properties from newProject to existedProject, excluding the "id", "code", "createdAt", "type"
        BeanUtils.copyProperties(newProject, existedProject, "id", "code", "createdAt", "type","memberShips", "tasks");

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
        boolean hasEmployees = project.getMemberShips().stream()
                .anyMatch(memberShip -> !memberShip.getProjectRole().equals(ProjectRole.OWNER));

        if (hasEmployees) {
            throw new RuntimeException("Cannot delete the project.still has employees associated with this project.");
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

    private void throwExceptionIfUserAlreadyExistsInProject(User user, Project project) {

        if (project.getMemberShips().stream()
                .anyMatch(memberShip -> memberShip.getUser().equals(user))) {
            throw new RuntimeException("User already exists in the project.");
        }
    }

    private MemberShip addUserToProject(User user, Project project, ProjectRole projectRole) {
        throwExceptionIfUserAlreadyExistsInProject(user, project);

        MemberShip newMemberShip = new MemberShip();
        newMemberShip.setProjectRole(projectRole);
        newMemberShip.setProject(project);
        newMemberShip.setUser(user);

        project.getMemberShips().add(newMemberShip);
        user.getMemberShips().add(newMemberShip);

        return memberShipService.save(newMemberShip);
    }



    public MemberShipResponse addUserToProject(MemberShipRequest memberShipRequest) {

        User user = userService.getByUserName(memberShipRequest.getUserName());
        Project project = getById(memberShipRequest.getProjectId());

        MemberShip newMemberShip = addUserToProject(user, project, memberShipRequest.getProjectRole());

        return memberShipService.toResponse(newMemberShip);
    }
    public void deleteUserFromProject(Long userId, Long projectId) {

        User user = userService.getById(userId);
        Project project = getById(projectId);

        MemberShip membershipToRemove = memberShipService.getByUserIdAndProjectId(user.getId(), project.getId());

        // Remove the membership from the project and the user
        project.getMemberShips().remove(membershipToRemove);
        user.getMemberShips().remove(membershipToRemove);

        memberShipService.delete(membershipToRemove.getId());
    }

    public List<MemberShipResponse> getResponseAllByProjectId(Long projectId) {
        Project project = getById(projectId);
        return project.getMemberShips().stream()
                .map(memberShipService::toResponse)
                .toList();
    }


    public TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId) {

        Task newTask = taskService.create(taskRequest);

        Project project = getById(projectId);
        project.getTasks().add(newTask);

        newTask.setProject(project);

        newTask = taskService.save(newTask);

        return taskService.toResponse(newTask);
    }



    public List<Task> getAllTasksByProjectId(Long projectId) {
        Project project = getById(projectId);
        return project.getTasks();
    }

    public List<TaskResponse> getResponseAllTasksByProjectId(Long projectId) {
        List<Task> projectTasks = getAllTasksByProjectId(projectId);
        return projectTasks.stream().map(taskService::toResponse).toList();
    }



}
