package com.edara.edara.model.dto;

import com.edara.edara.model.enums.MemberRole;
import com.edara.edara.model.enums.MemberType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
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

    @JsonProperty("user_code")
    @NotNull(message = "User code must not be null")
    private String userCode;

    @JsonProperty("title_id")
    private Long titleId;

    @JsonProperty("member_role")
    @NotNull(message = "Member role must not be null")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @JsonProperty("member_type")
    @NotNull(message = "Member type must not be null")
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @JsonProperty("employee_details")
    @Valid
    private EmployeeRequest employeeRequest;

}
