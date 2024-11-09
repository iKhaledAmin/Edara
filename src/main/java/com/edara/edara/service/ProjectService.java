package com.edara.edara.service;



import com.edara.edara.model.dto.*;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {
     ProjectResponse add(ProjectRequest projectRequest);


    MemberShipResponse addUserToProject(MemberShipRequest memberShipRequest);
    void deleteUserFromProject(Long userId, Long projectId);
     List<MemberShipResponse> getResponseAllUsersByProjectId(Long projectId);


     TaskResponse addTaskToProject(TaskRequest taskRequest, Long projectId);
     void deleteTaskFromProject(Long taskId);
     TaskResponse assignTaskToMember(Long taskId, Long userId);


    List<Task> getAllTasksByProjectId(Long projectId);
    List<TaskResponse> getResponseAllTasksByProjectId(Long projectId);


     List<Task> getAllTasksByUserId(Long userId);
     List<TaskResponse> getResponseAllTasksByUserId(Long userId);


     TitleResponse addTitleToProject(TitleRequest titleRequest, Long projectId);
     void deleteTitleFromProject(Long titleId);
     List<TitleResponse> getResponseAllTitlesByProjectId(Long projectId);
}
