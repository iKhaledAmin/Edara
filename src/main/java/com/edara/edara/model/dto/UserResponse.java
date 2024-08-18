package com.edara.edara.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse extends PersonResponse{


    @JsonProperty("personal_code")
    private String personalCode;

    @JsonProperty("number_of_projects")
    private Long NumberOfProjects;

    @JsonProperty("date_of_joining")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfJoining;
}
