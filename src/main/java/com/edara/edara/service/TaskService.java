package com.edara.edara.service;

import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Task;
import org.springframework.stereotype.Service;

@Service
public interface TaskService extends CrudService<TaskRequest, Task, TaskResponse,Long> {
    public TaskResponse toResponse(Task task) ;
    public Task toEntity(TaskRequest taskRequest);
    public Task create(TaskRequest taskRequest);
    public Task save(Task task);

}
