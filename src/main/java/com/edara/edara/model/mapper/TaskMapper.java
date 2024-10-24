package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.model.dto.TaskResponse;
import com.edara.edara.model.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(TaskRequest request);
    TaskResponse toResponse(Task entity);
}
