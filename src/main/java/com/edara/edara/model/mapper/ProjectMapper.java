package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.ProjectResponse;
import com.edara.edara.model.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
     Project toEntity(ProjectRequest request);

    @Mapping(target = "startedAt" , source = "entity.createdAt")
    ProjectResponse toResponse(Project entity);
}
