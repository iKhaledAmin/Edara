package com.edara.edara.service;


import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProjectService {

     ProjectResponse toResponse(Project project);
     Project toEntity(ProjectRequest projectRequest);


     /**
      * Adds a new project to the system and assigns an owner to it.
      * <p>
      * This method is responsible for persisting a new {@link Project} entity in the database and ensuring that an
      * authenticated user is assigned as the project's owner. The process involves several key steps:
      * </p>
      * <ol>
      *     <li>Saving the {@link Project} entity to generate a unique project ID.</li>
      *     <li>Creating a default {@link Title} ("Owner") associated with the project.</li>
      *     <li>Retrieving the currently authenticated user from the security context.</li>
      *     <li>Assigning the authenticated user as the project owner by creating a {@link Member} entity.</li>
      *     <li>Maintaining the bi-directional relationship between the project and its members.</li>
      * </ol>
      * <p>
      * This method ensures that each newly created project has an owner with an assigned title. The owner is determined
      * based on the currently authenticated user. The method is transactional to maintain data consistency.
      * </p>
      *
      * @param newProject The {@link Project} entity to be added to the system.
      * @return The saved {@link Project} instance, including its assigned owner.
      * @throws UsernameNotFoundException if the currently authenticated user cannot be found.
      */
     Project add(Project newProject);
     Project add(ProjectRequest projectRequest);


     Project update(Long projectId, Project newProject);
     Project update(Long projectId, ProjectRequest projectRequest);

     void delete(Long projectId);

     Optional<Project> getOptionalById(Long projectId);
     Project getById(Long projectId) ;
     ProjectResponse getResponseById(Long projectId);
}
