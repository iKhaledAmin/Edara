package com.edara.edara.model.entity;

import com.edara.edara.model.enums.ProjectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String code;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;


    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all MemberShips entities when the Project is deleted.
            orphanRemoval = true  // If you remove one of the MemberShip objects from the memberShips list
                                  // JPA will automatically delete that MemberShip from the database as well.
    )
    private List<MemberShip> memberShips = new ArrayList<>();

    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all Tasks entities when the Project is deleted.
            orphanRemoval = true
    )
    private List<Task> tasks = new ArrayList<>();


    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all titles entities when the Project is deleted.
            orphanRemoval = true
    )
    private List<Title> titles = new ArrayList<>();


}
