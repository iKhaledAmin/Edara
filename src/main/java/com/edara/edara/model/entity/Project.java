package com.edara.edara.model.entity;

import com.edara.edara.model.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @NotNull
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String code;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "aggregation_hour")
    private Integer aggregationHour = 12;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "started_at")
    private LocalDate startedDate;

    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all MemberShips entities when the Project is deleted.
            orphanRemoval = true  // If you remove one of the Member objects from the members list
                                  // JPA will automatically delete that Member from the database as well.
    )
    private List<Member> members = new ArrayList<>();

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


    @OneToMany(mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL //Deletes all currentAttendances entities when the Project is deleted.
    )
    private List<DailyAttendance> dailyAttendances  = new ArrayList<>();
}
