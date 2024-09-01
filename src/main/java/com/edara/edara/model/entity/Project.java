package com.edara.edara.model.entity;

import com.edara.edara.model.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String code;
    private ProjectType type;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
}
