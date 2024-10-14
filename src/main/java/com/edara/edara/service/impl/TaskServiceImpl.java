package com.edara.edara.service.impl;

import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Task;
import com.edara.edara.model.mapper.TaskMapper;
import com.edara.edara.repository.TaskRepo;
import com.edara.edara.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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

        return newTask;
    }

    public TaskResponse save(Task task) {
        taskRepo.save(task);
        return toResponse(task);
    }

    @Override
    public TaskResponse add(TaskRequest taskRequest) {
        Task newTask = create(taskRequest);
        save(newTask);
        return toResponse(newTask);
    }

    @Override
    public Task updateEntity(Long aLong, Task newEntity) {
        return null;
    }

    @Override
    public TaskResponse update(Long aLong, TaskRequest taskRequest) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Optional<Task> getEntityById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Task getById(Long aLong) {
        return null;
    }

    @Override
    public TaskResponse getResponseById(Long aLong) {
        return null;
    }

    @Override
    public List<TaskResponse> getAll() {
        return null;
    }
}
