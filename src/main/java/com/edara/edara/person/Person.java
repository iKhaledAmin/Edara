package com.edara.edara.person;


import com.edara.edara.global.BaseEntity;
import com.edara.edara.user.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;



@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @Column(nullable = false,updatable = false)
    private String firstName;

    @Column(nullable = false,updatable = false)
    private String lastName;

    @Column(unique = true, nullable = false,updatable = false)
    private String account;
    private String password;
    private String emailAddress;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String profession;
    private String country;
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfJoining;
}
