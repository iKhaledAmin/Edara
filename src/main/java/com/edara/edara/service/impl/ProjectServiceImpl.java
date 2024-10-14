package com.edara.edara.service.impl;

import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Task;
import com.edara.edara.model.mapper.ProjectMapper;
import com.edara.edara.repository.ProjectRepo;
import com.edara.edara.service.ProjectService;
import com.edara.edara.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

//    private void throwExceptionIfUserHasProjetWithSameName(String projectName) {
//        getEntityByprojectName(projectName)
//                .ifPresent(user -> { throw new RuntimeException("This user already has project with the same name.");
//                });
//    }

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
    public ProjectResponse add(ProjectRequest projectRequest) {
        //throwExceptionIfUserHasProjetWithSameName(projectRequest.getName());
        Project newProject = projectMapper.toEntity(projectRequest);
        newProject.setCode(generateUniqueProjectCode());
        newProject.setCreatedAt(new Date());
        projectRepo.save(newProject);

        return projectMapper.toResponse(newProject);
    }

    @Override
    public Project updateEntity(Long projectId, Project newProject) {
        Project existedProject = getById(projectId);

        // Copy properties from newProject to existedProject, excluding the "id", "code", "createdAt", "type"
        BeanUtils.copyProperties(newProject, existedProject, "id", "code", "createdAt", "type");

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

    @Override
    public void delete(Long projectId) {
        getById(projectId);
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


    public TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId) {

        Task newTask = taskService.create(taskRequest);

        Project project = getById(projectId);
        project.getTasks().add(newTask);

        projectRepo.save(project);

        return taskService.toResponse(newTask);
    }
}
