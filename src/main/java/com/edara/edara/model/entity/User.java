package com.edara.edara.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "user")
@PrimaryKeyJoinColumn(name = "user_id")
public class User extends Person{


    private String profession;
    private String userCode;
    private Long NumberOfProjects;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfJoining;



    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all MemberShips entities when the Project is deleted.
            orphanRemoval = true  // If you remove one of the MemberShip objects from the memberShips list
            // JPA will automatically delete that MemberShip from the database as well.
    )
    private List<MemberShip> memberShips = new ArrayList<>();
}
