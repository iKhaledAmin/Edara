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

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String uerName;

    @JsonProperty("project_name")
    private String projectName;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("join_date")
    private Date joinDate;

    @JsonProperty("project_role")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

    private String title;



}
