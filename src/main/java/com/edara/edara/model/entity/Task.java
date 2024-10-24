package com.edara.edara.model.entity;

import com.edara.edara.model.enums.TaskStatus;
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
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String code;

    @JsonFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date deadline;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;
}
