package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberShipRequest {

    @JsonProperty("project_id")
    @NotNull(message = "Project id must not be null")
    private Long projectId;

    @JsonProperty("user_name")
    @NotNull(message = "User name must not be null")
    @NotEmpty(message = "User name must not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]+@edara\\.com$", message = "User name must follow the format username@edara.com")
    private String userName;

    @JsonProperty("project_role")
    @NotNull(message = "Project role must not be null")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;
}
