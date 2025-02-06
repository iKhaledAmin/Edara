package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.*;
import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.enums.TaskStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;
    private final TaskService taskService;
    private final MemberService memberService;
    private final UserServiceImpl userService;
    private final TitleService titleService;
    private final CurrentAttendanceService currentAttendanceService;
    private final DailyAttendanceService dailyAttendanceService;
    private final NonNullBeanUtils nonNullBeanUtils;



    @Transactional
    public void aggregateDailyAttendance(Member member, Project project) {
        dailyAttendanceService.aggregateDailyMemberAttendancesOfProject(member, project);
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
                        aggregateDailyAttendance(member, project);
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


    private void throwExceptionIfMemberAlreadyExistsInProject(User user, Project project) {
        if (project.getMembers() != null && project.getMembers().stream()
                .anyMatch(member -> member.getUser().equals(user))) {
            throw new ConflictException("Member already exists in the project.");
        }
    }

    @Transactional
    @Override
    public MemberResponse addMemberToProject(MemberRequest memberRequest) {
        Project project = getById(memberRequest.getProjectId());
        User user = userService.getByCode(memberRequest.getUserCode());

        throwExceptionIfMemberAlreadyExistsInProject(user, project);

        Title title = (memberRequest.getTitleId() != null) ? titleService.getById(memberRequest.getTitleId()) : null;

        EmployeeRequest employeeRequest = memberRequest.getEmployeeRequest();

        Member newMember = memberService.add(
                user,
                project,
                memberRequest.getMemberRole(),
                memberRequest.getMemberType(),
                title,
                (employeeRequest != null) ? employeeRequest.getType() : null,
                (employeeRequest != null) ? employeeRequest.getBaseSalary() : null,
                (employeeRequest != null) ? employeeRequest.getBonusSalary() : null
        );

        return memberService.toResponse(newMember);
    }

    @Override
    public MemberResponse updateMemberOfProject(MemberRequest memberRequest) {
        if (memberRequest == null)
            return null;

        getById(memberRequest.getProjectId());
        userService.getByCode(memberRequest.getUserCode());
        Member member = memberService.getByUserCodeAndProjectId(memberRequest.getUserCode(), memberRequest.getProjectId());

        return memberService.update(member.getId(), memberRequest);

    }

    public MemberResponse getResponseMemberOfProjectByMemberId(Long memberId) {
        return memberService.getResponseById(memberId);
    }

    private void throwExceptionIfMemberStillWorkingOnTask(Member member) {
        if (member.getTasks().stream()
                .anyMatch(task -> task.getStatus().equals(TaskStatus.ON_WORKING))) {
            throw new ConflictException("Member is still working on a task.");
        }
    }


    @Transactional
    @Override
    public void deleteMemberFromProject(String userCode, Long projectId) {
        User user = userService.getByCode(userCode);
        Project project = getById(projectId);

        Member member = memberService.getByUserCodeAndProjectId(userCode, projectId);
        throwExceptionIfMemberStillWorkingOnTask((member));

        DailyAttendance dailyAttendance = dailyAttendanceService.getCurrentAttendanceByMemberIdAndProjectId( member.getId(), projectId);
        if (dailyAttendance != null) {
            dailyAttendanceService.endAttendance(member, project);
        }

        user.getMembers().remove(member);
        project.getMembers().remove(member);

        //memberService.deleteById(membershipId); //no need for this because orphanRemoval = true in the relation
        // Member and (Project and User) .
    }

    @Override
    public List<MemberResponse> getResponseAllMembersByProjectId(Long projectId) {
        Project project = getById(projectId);
        return project.getMembers().stream()
                .map(memberService::toResponse)
                .toList();
    }
    public MemberResponse getResponseMemberByMemberId(Long memberId) {
        return memberService.getResponseById(memberId);
    }

    private void throwExceptionIfUserNotInvolvedInThisProject(String userCode, Long projectId) {

        if (!memberService.getEntityByUserCodeAndProjectId(userCode, projectId).isPresent()) {
            throw new ConflictException("User with code = " + userCode + " not involved in this project.");
        }
    }
    @Override
    public CurrentAttendanceResponse recordMemberAttendance(String userCode, Long projectId) {

        Project project = getById(projectId);
        User user = userService.getByCode(userCode);
        Optional<Member> member = memberService.getEntityByUserIdAndProjectId(user.getId(), projectId);
        throwExceptionIfUserNotInvolvedInThisProject(userCode, projectId);

        return currentAttendanceService.toResponse(dailyAttendanceService.recordAttendance(member.get(), project));
    }
    public CurrentAttendanceResponse endMemberAttendance(String userCode, Long projectId) {
        Project project = getById(projectId);
        User user = userService.getByCode(userCode);
        Optional<Member> member = memberService.getEntityByUserIdAndProjectId(user.getId(), projectId);

        throwExceptionIfUserNotInvolvedInThisProject(userCode, projectId);

        return currentAttendanceService.toResponse(dailyAttendanceService.endAttendance(member.get(),project));
    }
    @Override
    public List<DailyAttendanceResponse> getAllDailyAttendancesByProjectIdAndUserCode(Long projectId, String userCode, Integer year, Integer month)  {
        getById(projectId);
        userService.getByCode(userCode);
        Member member = memberService.getByUserCodeAndProjectId(userCode, projectId);
        return dailyAttendanceService.getAllByProjectIdAndMemberId(projectId, member.getId(), year, month)
                .stream()
                .map(dailyAttendanceService::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }
    @Override
    public List<DailyAttendanceResponse> getAllDailyAttendancesByProjectId(Long projectId, LocalDateTime date){
        getById(projectId);
        return dailyAttendanceService.getAllByProjectIdAndIsAggregatedTrue(projectId, date)
                .stream()
                .map(dailyAttendanceService::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }
    @Override
    public List<DailyAttendanceResponse> getAllCurrentAttendancesByProjectId(Long projectId){
        getById(projectId);
        return dailyAttendanceService.getAllCurrentAttendancesByProjectId(projectId).stream()
                .map(dailyAttendanceService::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }

    @Override
    public List<DailyAttendanceResponse> getAllAbsencesByProjectIdAndUserCode(Long projectId, String userCode) {

        getById(projectId);
        userService.getByCode(userCode);
        Member member = memberService.getByUserCodeAndProjectId(userCode, projectId);

        return dailyAttendanceService.getAllAbsencesByProjectIdAndMemberId(projectId, member.getId())
                .stream()
                .map(dailyAttendanceService::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }

    @Override
    public  List<DailyAttendanceResponse> getAllAbsencesByProjectId(Long projectId, LocalDate date) {

        getById(projectId);
        return dailyAttendanceService.getAllAbsencesByProjectId(projectId, date)
                .stream()
                .map(dailyAttendanceService::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
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

        if (task.getStatus().equals(TaskStatus.ON_WORKING)) {
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
    private Task assignTaskToMember(Task task, Member member) {

        throwExceptionIfTaskAlreadyAssignedToEmployee(task);
        task.setMember(member);
        task.setStatus(TaskStatus.ON_WORKING);

        member.getTasks().add(task);

        return taskService.save(task);
    }
    public TaskResponse assignTaskToMember(Long taskId, Long userId) {
        Task task = taskService.getById(taskId);
        User user = userService.getById(userId);

        Member member = memberService.getEntityByUserIdAndProjectId(userId, task.getProject().getId()).orElseThrow(
                () -> new RuntimeException("User with id = " + userId + " not involved in this project.")
        );

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
        return taskService.getAllByUserId(userId);
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
