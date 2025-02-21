package com.edara.edara.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TaskService  {

    public Task toEntity(TaskRequest taskRequest);
    public TaskResponse toResponse(Task task);

    Task add(TaskRequest taskRequest);
    TaskResponse update(Long taskId, TaskRequest taskRequest);
    TaskResponse addTaskToProject(TaskRequest taskRequest);
    TaskResponse assignTaskToMember(Long taskId,String userCode);
    void delete(Long taskId);
    Optional<Task> getOptionalById(Long taskId);
    Task getById(Long taskId);
    TaskResponse getResponseById(Long taskId);
     List<Task> getAllByUserCode(String userCode);

     List<Task> getAllByProjectId(Long projectId);
     List<Task> getAllByUserCodeAndProjectId(String userCode, Long projectId);
     List<TaskResponse> getResponseAllByUserCode(String userCode);
     List<TaskResponse> getResponseAllByProjectId(Long projectId);
     List<TaskResponse> getResponseAllByUserCodeAndProjectId(String userCode,Long projectId);


    TaskResponse finishTask(Long taskId);

}
