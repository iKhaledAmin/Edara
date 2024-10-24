package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberShipResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String uerName;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("project_role")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;



}
