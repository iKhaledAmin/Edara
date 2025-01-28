package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {

    @JsonProperty("project_id")
    @NotNull(message = "Project id must not be null")
    private Long projectId;

    @JsonProperty("user_id")
    @NotNull(message = "User id must not be null")
    private Long userId;

    @JsonProperty("title_id")
    private Long titleId;

    @JsonProperty("project_role")
    @NotNull(message = "Project role must not be null")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

}
