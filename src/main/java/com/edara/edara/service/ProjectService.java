package com.edara.edara.service;

import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Project;
import org.springframework.stereotype.Service;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {
    public TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId);
}
