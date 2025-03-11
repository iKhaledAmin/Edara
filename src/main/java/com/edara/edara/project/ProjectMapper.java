package com.edara.edara.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "members", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "tasks", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "titles", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "dailyAttendances", expression = "java(new java.util.ArrayList<>())")
     Project toEntity(ProjectRequest request);

    @Mapping(target = "startedAt" , source = "entity.createdAt")
    ProjectResponse toResponse(Project entity);
}
