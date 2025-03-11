package com.edara.edara.member;

import com.edara.edara.attendance.DailyAttendance;
import com.edara.edara.global.BaseEntity;
import com.edara.edara.project.Project;
import com.edara.edara.task.Task;
import com.edara.edara.title.Title;
import com.edara.edara.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role",nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type",nullable = false)
    private MemberType memberType;

    @Column(name = "join_date",nullable = false)
    private LocalDate joinDate;


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


    @OneToMany(mappedBy = "member",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne( fetch = FetchType.LAZY,
            optional = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "title_id", referencedColumnName = "title_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Title title;

//    @OneToMany(mappedBy = "member",
//            fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL //Deletes all currentAttendances entities when the Member is deleted.
//    )
//    private List<CurrentAttendance> currentAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "member",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL //Deletes all dailyAttendances entities when the Member is deleted.
    )
    private List<DailyAttendance> dailyAttendances  = new ArrayList<>();

    @OneToOne(
            mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER,
            optional = true
    )
    private Employee employee;

}
