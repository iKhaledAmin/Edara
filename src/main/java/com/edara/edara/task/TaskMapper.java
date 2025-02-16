package com.edara.edara.task;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(TaskRequest request);
    @Mapping(target = "projectName" , source = "entity.project.name" )
    TaskResponse toResponse(Task entity);

    @AfterMapping
    default void setMemberName(@MappingTarget TaskResponse response, Task entity) {
        if (entity.getMember() != null && entity.getMember().getUser() != null) {
            response.setMemberName(entity.getMember().getUser().getFirstName() + " " + entity.getMember().getUser().getLastName());
        }
    }
}
