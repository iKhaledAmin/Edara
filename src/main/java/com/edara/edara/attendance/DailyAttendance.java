package com.edara.edara.attendance;

import com.edara.edara.member.Member;
import com.edara.edara.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "daily_attendance")
public class DailyAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_attendance_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration period;

    @ColumnDefault("false")
    @Column(name = "is_aggregated", nullable = false)
    private Boolean isAggregated = false;


    @OneToMany(mappedBy = "dailyAttendance",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL //Deletes all currentAttendances entities when the DailyAttendance is deleted.
    )
    private List<CurrentAttendance> currentAttendances = new ArrayList<>();

    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false,updatable = false)
    private Member member;

    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false,updatable = false)
    private Project project;
}
