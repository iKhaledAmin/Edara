package com.edara.edara.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "user")
@PrimaryKeyJoinColumn(name = "user_id")
public class User extends Person{

    private String userCode;

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, //Deletes all MemberShips entities when the Project is deleted.
            orphanRemoval = true  // If you remove one of the Member objects from the members list
            // JPA will automatically delete that Member from the database as well.
    )
    private List<Member> members = new ArrayList<>();
}
