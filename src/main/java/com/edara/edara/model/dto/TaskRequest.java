package com.edara.edara.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Name must not be empty")
    @NotBlank(message = "Name must not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotEmpty(message = "Description must not be empty")
    @NotBlank(message = "Description must not be empty")
    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    private String description;

    @Future(message = "Deadline must be a future date")
    @JsonFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date deadline;

}
