package com.edara.edara.model.dto;


import com.edara.edara.model.enums.Gender;
import com.edara.edara.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String userCode;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;


    @JsonProperty("account")
    private String account; ;

    @JsonProperty("password")
    private String password;


    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("birthday")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @JsonProperty("image")
    private byte[] image;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("profession")
    private String profession;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("date_of_joining")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfJoining;
}
