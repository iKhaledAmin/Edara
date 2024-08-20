package com.edara.edara.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserRequest extends PersonRequest{

    @JsonProperty("personal_code")
    private String personalCode;

    @JsonProperty("number_of_projects")
    private Long NumberOfProjects;
}