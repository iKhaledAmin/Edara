package com.edara.edara.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("member_name")
    private String memberName;

    @JsonProperty("project_name")
    private String projectName;
}
