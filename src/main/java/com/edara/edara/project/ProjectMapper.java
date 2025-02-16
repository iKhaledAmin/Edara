package com.edara.edara.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
     Project toEntity(ProjectRequest request);

    @Mapping(target = "startedAt" , source = "entity.createdAt")
    ProjectResponse toResponse(Project entity);
}
