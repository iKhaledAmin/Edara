package com.edara.edara.service;



import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {

    MemberShipResponse addUserToProject(MemberShipRequest memberShipRequest);
    void deleteUserFromProject(Long userId, Long projectId);

     List<MemberShipResponse> getResponseAllByProjectId(Long projectId);

     TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId);
     List<Task> getAllTasksByProjectId(Long projectId);
     List<TaskResponse> getResponseAllTasksByProjectId(Long projectId);

}
