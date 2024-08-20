package com.edara.edara.model.dto;


import com.edara.edara.model.enums.Gender;
import com.edara.edara.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;


    @JsonProperty("user_name")
    private String userName ;

    @JsonProperty("password")
    private String password;


    @JsonProperty("email")
    private String email;

    @JsonProperty("birthday")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    @JsonProperty("image")
    private String image;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("role")
    private Role role;
}
