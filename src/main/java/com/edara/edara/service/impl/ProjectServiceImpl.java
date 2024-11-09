package com.edara.edara.service.impl;

import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.*;
import com.edara.edara.model.enums.ProjectRole;
import com.edara.edara.model.enums.TaskStatus;
import com.edara.edara.model.mapper.ProjectMapper;
import com.edara.edara.repository.ProjectRepo;
import com.edara.edara.service.MemberShipService;
import com.edara.edara.service.ProjectService;
import com.edara.edara.service.TaskService;
import com.edara.edara.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final TitleService titleService;



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
    public ProjectResponse toResponse(Project project) {
        return projectMapper.toResponse(project);
    }

    @Override
    public Project toEntity(ProjectRequest projectRequest) {
        return projectMapper.toEntity(projectRequest);
    }

    @Override
    public Project create(ProjectRequest projectRequest) {

        throwExceptionIfUserHasProjectWithSameName(projectRequest.getName());

        Project newProject = toEntity(projectRequest);
        newProject.setCode(generateUniqueProjectCode());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUserName(authentication.getName());

        MemberShip memberShip = addUserToProject (user, newProject, ProjectRole.OWNER, null);
        newProject.getMemberShips().add(memberShip);

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

    private MemberShip addUserToProject(User user, Project project, ProjectRole projectRole, Title title) {
        throwExceptionIfUserAlreadyExistsInProject(user, project);

        MemberShip newMemberShip = new MemberShip();
        newMemberShip.setProjectRole(projectRole);
        newMemberShip.setProject(project);
        newMemberShip.setUser(user);
        newMemberShip.setTitle(title);

        project.getMemberShips().add(newMemberShip);
        user.getMemberShips().add(newMemberShip);

        return memberShipService.save(newMemberShip);
    }

    @Override
    public MemberShipResponse addUserToProject(MemberShipRequest memberShipRequest) {

        User user = userService.getById(memberShipRequest.getUserId());
        Project project = getById(memberShipRequest.getProjectId());
        Title title = titleService.getById(memberShipRequest.getTitleId());

        MemberShip newMemberShip = addUserToProject(user, project, memberShipRequest.getProjectRole(), title);

        return memberShipService.toResponse(newMemberShip);
    }
    @Override
    public void deleteUserFromProject(Long userId, Long projectId) {

        User user = userService.getById(userId);
        Project project = getById(projectId);

        MemberShip membershipToRemove = memberShipService.getByUserIdAndProjectId(user.getId(), project.getId());

        // Remove the membership from the project and the user
        project.getMemberShips().remove(membershipToRemove);
        user.getMemberShips().remove(membershipToRemove);

        memberShipService.delete(membershipToRemove.getId());
    }

    @Override
    public List<MemberShipResponse> getResponseAllUsersByProjectId(Long projectId) {
        Project project = getById(projectId);
        return project.getMemberShips().stream()
                .map(memberShipService::toResponse)
                .toList();
    }


    private void throwExceptionIfProjectIncludeTaskWithSameName(String taskName , Project project) {

        boolean hasSameTaskName = project.getTasks().stream()
                .anyMatch(task -> task.getName().equalsIgnoreCase(taskName));

        if (hasSameTaskName) {
            throw new RuntimeException("Task with same name already exists in this project.");
        }
    }
    @Override
    public TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId) {

        Project project = getById(projectId);
        throwExceptionIfProjectIncludeTaskWithSameName(taskRequest.getName(), project);

        Task newTask = taskService.create(taskRequest);

        project.getTasks().add(newTask);
        newTask.setProject(project);

        newTask = taskService.save(newTask);
        if (taskRequest.getEmployeeId() != null) {
            return assignTaskToMember(newTask.getId(), taskRequest.getEmployeeId());
        }
        return taskService.toResponse(newTask);
    }

    private void throwExceptionIfTaskOnWorking(Task task) {

        if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
            throw new RuntimeException("Task is already in on working.");
        }
    }

    @Override
    @Transactional
    public void deleteTaskFromProject(Long taskId) {
        Task task = taskService.getById(taskId);
        throwExceptionIfTaskOnWorking(task);
        Project project = getById(task.getProject().getId());
        project.getTasks().remove(task); // This triggers deletion of task due to orphanRemoval = true
    }

    private void throwExceptionIfTaskAlreadyAssignedToEmployee(Task task) {

        if (task.getMember() != null) {
            throw new RuntimeException("Task already assigned to employee.");
        }

    }
    private Task assignTaskToMember(Task task, MemberShip member) {

        throwExceptionIfTaskAlreadyAssignedToEmployee(task);
        task.setMember(member);
        task.setStatus(TaskStatus.IN_PROGRESS);

        member.getTasks().add(task);

        return taskService.save(task);
    }
    public TaskResponse assignTaskToMember(Long taskId, Long userId) {
        Task task = taskService.getById(taskId);
        MemberShip member = memberShipService.getByUserIdAndProjectId(userId, task.getProject().getId());
        Task aasignedTask = assignTaskToMember(task, member);

        return taskService.toResponse(aasignedTask);
    }

    public List<Task> getAllTasksByProjectId(Long projectId) {
        Project project = getById(projectId);
        return project.getTasks();
    }

    public List<TaskResponse> getResponseAllTasksByProjectId(Long projectId) {
        List<Task> projectTasks = getAllTasksByProjectId(projectId);
        return projectTasks.stream().map(taskService::toResponse).toList();
    }

    public List<Task> getAllTasksByUserId(Long userId) {
        return taskService.getAllTasksByUserId(userId);
    }
    public List<TaskResponse> getResponseAllTasksByUserId(Long userId) {
        List<Task> projectTasks = getAllTasksByUserId(userId);
        return projectTasks.stream().map(taskService::toResponse).toList();
    }

    private void throwExceptionIfProjectIncludeTitleWithSameName(String titleName, Project project) {
        boolean hasSameTitleName = project.getTitles().stream()
                .anyMatch(title -> title.getName().equalsIgnoreCase(titleName));

        if (hasSameTitleName) {
            throw new RuntimeException("Title with the same name already exists in this project.");
        }
    }

    @Transactional
    public TitleResponse addTitleToProject(TitleRequest titleRequest, Long projectId) {
        Project project = getById(projectId);
        throwExceptionIfProjectIncludeTitleWithSameName(titleRequest.getName(), project);
        Title title = titleService.add(titleRequest, project);
        project.getTitles().add(title);

        return titleService.toResponse(title);
    }


    @Override
    @Transactional
    public void deleteTitleFromProject(Long titleId) {
        Title title = titleService.getById(titleId);
        Project project = getById(title.getProject().getId());
        project.getTitles().remove(title); // This triggers deletion of task due to orphanRemoval = true
    }

    public List<TitleResponse> getResponseAllTitlesByProjectId(Long projectId){
        Project project = getById(projectId);
        return project.getTitles().stream().map(titleService::toResponse).toList();
    }
}
