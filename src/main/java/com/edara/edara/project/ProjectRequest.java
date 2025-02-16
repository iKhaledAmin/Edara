package com.edara.edara.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Name must not be empty")
    @NotBlank(message = "Name must not be empty")
    @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotEmpty(message = "Description must not be empty")
    @NotBlank(message = "Description must not be empty")
    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    private String description;

    @NotNull(message = "Project type must not be mull")
    @JsonProperty("type")
    private ProjectType type;


    @Range(min = 0, max = 23, message = "Aggregation hour must be between 0 and 23")
    @NotNull(message = "Aggregation hour must not be null")
    @JsonProperty("aggregation_hour")
    private Integer aggregationHour;

    @JsonProperty("image")
    private byte[] image;
}
