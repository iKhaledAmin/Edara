package com.edara.edara.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "user")
@PrimaryKeyJoinColumn(name = "user_id")
public class User extends Person{


    private String profession;
    private String personalCode;
    private Long NumberOfProjects;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfJoining;



    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<MemberShip> memberShips;
}
