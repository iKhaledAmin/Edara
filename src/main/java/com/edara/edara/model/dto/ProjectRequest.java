package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {


    @NotNull(message = "Project name must not be null")
    @NotEmpty(message = "Project name must not be mpty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Project name must contain only letters")
    @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotEmpty(message = "Description must not be empty")
    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    private String description;

    @NotNull(message = "Project type must not be mull")
    @JsonProperty("type")
    private ProjectType type;

    @JsonProperty("image")
    private String image;

}
