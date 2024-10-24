package com.edara.edara.service.impl;

import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Task;
import com.edara.edara.model.enums.TaskStatus;
import com.edara.edara.model.mapper.TaskMapper;
import com.edara.edara.repository.TaskRepo;
import com.edara.edara.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    public Task toEntity(TaskRequest taskRequest) {
        return taskMapper.toEntity(taskRequest);
    }
    public TaskResponse toResponse(Task task) {
        return taskMapper.toResponse(task);
    }

    public Task create(TaskRequest taskRequest){
        Task newTask = taskMapper.toEntity(taskRequest);
        newTask.setCode(generateUniqueTaskCode());
        newTask.setStatus(TaskStatus.PENDING);
        return newTask;
    }

    public Task save(Task task) {
        return taskRepo.save(task);
    }

    @Override
    public TaskResponse add(TaskRequest taskRequest) {
        Task newTask = create(taskRequest);
        return toResponse(save(newTask));
    }

    @Override
    public Task updateEntity(Long taskId, Task newTask) {
        Task existedTask = getById(taskId);

        // Copy properties from newTask to existedTask, excluding the "id", "code", "project"
        BeanUtils.copyProperties(newTask, existedTask, "id", "code",  "project");

        // Save the updated task
        //existedUser = userRepo.save(existedUser);

        return save(existedTask);
    }

    @Override
    public TaskResponse update(Long taskId, TaskRequest taskRequest) {
        Task newTask = toEntity(taskRequest);

        Task updatedTask = updateEntity(taskId,newTask);

        return toResponse(updatedTask);
    }

    @Override
    public void delete(Long taskId) {
        getById(taskId);
        taskRepo.deleteById(taskId);
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
    public List<TaskResponse> getAll() {
        return taskRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
