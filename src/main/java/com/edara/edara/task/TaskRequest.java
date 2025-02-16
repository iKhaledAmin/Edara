package com.edara.edara.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @JsonProperty("project_id")
    @NotNull(message = "Project id must not be null")
    private Long projectId;

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Name must not be empty")
    @NotBlank(message = "Name must not be empty")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotEmpty(message = "Description must not be empty")
    @NotBlank(message = "Description must not be empty")
    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    @JsonProperty("description")
    private String description;

    @Future(message = "Deadline must be a future date")
    //@JsonFormat(pattern="yyyy-MM-dd-hh-mm")
    @JsonFormat(pattern="yyyy-MM-dd-HH-mm")
    @NotNull(message = "Deadline must not be null")
    @JsonProperty("deadline")
    private LocalDateTime deadline;

    @JsonProperty("user_code")
    private String userCode;

}
