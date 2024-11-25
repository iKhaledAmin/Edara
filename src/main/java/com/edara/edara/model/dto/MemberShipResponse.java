package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectRole;
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
public class MemberShipResponse {

    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_image")
    private String employeeImage;

    @JsonProperty("employee_code")
    private String employeeCode;

    @JsonProperty("project_role")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

    @JsonProperty("title")
    private String title;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("join_date")
    private Date joinDate;

}
