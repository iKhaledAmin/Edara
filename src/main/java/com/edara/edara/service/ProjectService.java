package com.edara.edara.service;


import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.model.dto.TitleResponse;
import com.edara.edara.model.entity.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService extends CrudService<ProjectRequest, Project, ProjectResponse,Long> {
     ProjectResponse add(ProjectRequest projectRequest);


     TitleResponse addTitleToProject(TitleRequest titleRequest, Long projectId);
     void deleteTitleFromProject(Long titleId);
     List<TitleResponse> getResponseAllTitlesByProjectId(Long projectId);
}
