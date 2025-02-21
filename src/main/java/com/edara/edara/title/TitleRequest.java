package com.edara.edara.title;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder

@AllArgsConstructor
@NoArgsConstructor
public class TitleRequest {

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Name must not be null")
    @NotBlank(message = "Name must not be null")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @NotEmpty(message = "Description must not be null")
    @NotBlank(message = "Description must not be null")
    @Size(max = 65535, message = "Description must not exceed 65535 characters")
    private String description;
}
