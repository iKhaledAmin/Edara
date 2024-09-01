package com.edara.edara.model.dto;

import com.edara.edara.model.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("code")
    private String code;

    @JsonProperty("type")
    private ProjectType type;

    @JsonProperty("image")
    private String image;

    @JsonProperty("created_at")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
}
