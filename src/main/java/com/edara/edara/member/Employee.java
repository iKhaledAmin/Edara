package com.edara.edara.member;

import com.edara.edara.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeType type;

    @Column(nullable = false)
    private Double bonusSalary;

    @Column(nullable = false)
    private Double baseSalary;

    @Column(nullable = false)
    private Double totalSalary;


    @OneToOne(
            optional = false
    )
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, unique = true)
    private Member member;

}