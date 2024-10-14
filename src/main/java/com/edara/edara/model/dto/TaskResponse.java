package com.edara.edara.model.dto;

import com.edara.edara.model.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private Long id;
    private String name;
    private String description;
    private String code;

    @JsonFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date deadline;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}
