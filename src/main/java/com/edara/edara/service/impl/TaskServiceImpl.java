package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Task;
import com.edara.edara.model.enums.TaskStatus;
import com.edara.edara.model.mapper.TaskMapper;
import com.edara.edara.repository.TaskRepo;
import com.edara.edara.service.*;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;
    private final TaskMapper taskMapper;
    private final NonNullBeanUtils nonNullBeanUtils;
    private final ServiceLocator serviceLocator;


    private Long getNextId(){
        Long lastId = taskRepo.getLastId();
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

    private String generateUniqueTaskCode() {
        Long nextId = getNextId();
        String sequenceNumber = hashIdToSixDigit(nextId.toString());
        return sequenceNumber;
    }

    @Override
    public Task toEntity(TaskRequest taskRequest) {
        return taskMapper.toEntity(taskRequest);
    }

    @Override
    public TaskResponse toResponse(Task task) {
        return taskMapper.toResponse(task);
    }

    private Task create(TaskRequest taskRequest) {
        Task newTask = toEntity(taskRequest);
        newTask.setCode(generateUniqueTaskCode());
        newTask.setStatus(TaskStatus.WAITING);
        return newTask;
    }

    private Task save(Task task) {
        return taskRepo.save(task);
    }

    @Override
    public Task add(TaskRequest taskRequest) {
        Task newTask = create(taskRequest);
        return save(newTask);
    }


    private void throwExceptionIfProjectIncludesActiveTaskWithSameName(String taskName, Project project) {
        boolean hasActiveTaskWithSameName = project.getTasks().stream()
                .anyMatch(task -> task.getName().equalsIgnoreCase(taskName) && task.getStatus() != TaskStatus.FINISHED);

        if (hasActiveTaskWithSameName) {
            throw new ConflictException("The project already contains an active task with the same name.");
        }
    }
    @Override
    public TaskResponse addTaskToProject(TaskRequest taskRequest) {
        Project project = serviceLocator.getService(ProjectService.class).getById(taskRequest.getProjectId());

        throwExceptionIfProjectIncludesActiveTaskWithSameName(taskRequest.getName(), project);

        Task newTask = create(taskRequest);
        newTask.setProject(project);
        project.getTasks().add(newTask);

        newTask = save(newTask);
        if (taskRequest.getUserCode() != null) {
            return assignTaskToMember(newTask.getId(),taskRequest.getUserCode());
        }

        return toResponse(newTask);
    }

    private void throwExceptionIfTaskAlreadyAssignedToMember(Task task) {

        if (task.getMember() != null) {
            throw new ConflictException("Task already assigned to another member.");
        }

    }
    private Task assignTaskToMember(Task task, Member member) {

        throwExceptionIfTaskAlreadyAssignedToMember(task);
        task.setMember(member);
        task.setStatus(TaskStatus.ON_WORKING);

        member.getTasks().add(task);

        return save(task);
    }
    public TaskResponse assignTaskToMember(Long taskId,String userCode) {

        serviceLocator.getService(UserService.class).getByCode(userCode);
        Task task = getById(taskId);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, task.getProject().getId());

        Task aasignedTask = assignTaskToMember(task, member);
        return toResponse(aasignedTask);
    }

    @SneakyThrows
    private Task updateEntity(Long taskId, Task newTask) {
        Task existedTask = getById(taskId);

        throwExceptionIfTaskAlreadyAssignedToMember(existedTask);

        // Copy properties from newTask to existedTask, excluding the "id", "code", "project"
        nonNullBeanUtils.copyProperties(newTask, existedTask, "id", "code","name","project","member");

        return save(existedTask);
    }

    @Override
    public TaskResponse update(Long taskId, TaskRequest taskRequest) {
        Task newTask = toEntity(taskRequest);
        Task updatedTask = updateEntity(taskId,newTask);
        return toResponse(updatedTask);
    }



    private void throwExceptionIfTaskOnWorking(Task task) {

        if (task.getStatus() != TaskStatus.WAITING) {
            throw new RuntimeException("can't delete task may be on working or already finished");
        }
    }
    @Override
    @Transactional
    public void delete(Long taskId) {
        Task task = getById(taskId);
        throwExceptionIfTaskOnWorking(task);
        taskRepo.deleteById(taskId);
    }

    private Task changeStatus(Long taskId, TaskStatus status) {
        Task task = getById(taskId);
        task.setStatus(status);
        return save(task);
    }
    private void throwExceptionIfTaskAlreadyFinished(Task task) {

        if (task.getStatus() == TaskStatus.FINISHED) {
            throw new ConflictException("Task already finished.");
        }
    }
    public TaskResponse finishTask(Long taskId) {
        throwExceptionIfTaskAlreadyFinished(getById(taskId));
        return toResponse(changeStatus(taskId, TaskStatus.FINISHED));
    }




    @Override
    public Optional<Task> getEntityById(Long taskId) {
        return taskRepo.findById(taskId);
    }

    @Override
    public Task getById(Long taskId) {
        return getEntityById(taskId).orElseThrow(
                () -> new NoSuchElementException("There is no task with id  = " + taskId)
        );
    }

    @Override
    public TaskResponse getResponseById(Long taskId) {
        return toResponse(getById(taskId));
    }

    @Override
    public List<Task> getAllByUserCode(String userCode) {
        serviceLocator.getService(UserService.class).getByCode(userCode);
        return taskRepo.findByMember_User_UserCode(userCode);
    }

    @Override
    public List<Task> getAllByProjectId(Long projectId) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        return taskRepo.findByProject_Id(projectId);
    }

    @Override
    public List<Task> getAllByUserCodeAndProjectId(String userCode, Long projectId) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        serviceLocator.getService(UserService.class).getByCode(userCode);
        return taskRepo.findByMember_User_UserCodeAndProject_Id(userCode, projectId);
    }

    @Override
    public List<TaskResponse> getResponseAllByUserCode(String userCode) {
        List<Task> projectTasks = getAllByUserCode(userCode);
        return projectTasks.stream().map(this::toResponse).toList();
    }

    @Override
    public List<TaskResponse> getResponseAllByProjectId(Long projectId) {
        List<Task> tasks = getAllByProjectId(projectId);
        return tasks.stream().map(this::toResponse).toList();
    }

    @Override
    public List<TaskResponse> getResponseAllByUserCodeAndProjectId(String userCode,Long projectId) {
        List<Task> tasks = getAllByUserCodeAndProjectId(userCode, projectId);
        return tasks.stream().map(this::toResponse).toList();
    }

}
