package com.edara.edara.model.entity;

import com.edara.edara.model.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "membership")
public class MemberShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;


    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;


    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;
}
