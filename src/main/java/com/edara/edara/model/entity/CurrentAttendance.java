package com.edara.edara.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "current_attendance")
public class CurrentAttendance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @Column(updatable = false,nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private Duration period;

    @ColumnDefault("false")
    @Column(name = "is_aggregated", nullable = false)
    private Boolean isAggregated = false;

    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false,updatable = false)
    private MemberShip member;

    @ManyToOne( fetch = FetchType.LAZY,
            optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false,updatable = false)
    private Project project;

    @ManyToOne( fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JoinColumn(name = "daily_attendance_id", referencedColumnName = "daily_attendance_id")
    private DailyAttendance dailyAttendance;

}
