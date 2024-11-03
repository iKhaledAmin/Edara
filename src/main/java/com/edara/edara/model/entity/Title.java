package com.edara.edara.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "title")
public class Title extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;


    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;
}
