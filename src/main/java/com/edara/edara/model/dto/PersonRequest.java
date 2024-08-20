package com.edara.edara.model.dto;

import com.edara.edara.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonRequest {

    @JsonProperty("first_name")
    @NotNull(message = "First Name Must Not Be Null")
    @NotEmpty(message = "First Name Must Not Be Empty")
    //@Size(min = 3 ,message = "First name Must be more than 3 letters")
    //@Size(max = 20 ,message = "First name Must be less than 20 letters")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "First Name Must Not Be Null")
    @NotEmpty(message = "First Name Must Not Be Empty")
    //@Size(min = 3 ,message = "First name Must be more than 3 letters")
    @Size(max = 20 ,message = "First name Must be less than 20 letters")
    private String lastName;


    @JsonProperty("user_name")
    //@Size(min = 5 ,message = "User name must be more than 5 letters")
    //@Size(max = 20 ,message = "Last name must be less than 20 letters")
    @NotNull(message = "User name Must Not Be Null")
    @NotEmpty(message = "User name Must Not Be Empty")
    private String userName ;

    @JsonProperty("password")
    @NotNull(message = "Password must not be null")
    @NotEmpty(message = "Password must not be empty")
    //@Size(max = 20, min = 8, message = "student_password Must Be Between 8 and 20 character.")
    private String password;


    @JsonProperty("email")
    @Email(message = "Invalid email address")
    private String email;

    @JsonProperty("birthday")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    @JsonProperty("image")
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("phone_number")
    @Pattern(regexp="(^$|[0-9]{11})" , message = "Invalid phone number")
    private String phoneNumber;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;


}
