package com.edara.edara.model.entity;


import com.edara.edara.model.enums.Gender;
import com.edara.edara.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String account ;
    private String password;
    private String email;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phoneNumber;
    private String country;
    private String city;
    @Enumerated(EnumType.STRING)
    private Role role;
}
