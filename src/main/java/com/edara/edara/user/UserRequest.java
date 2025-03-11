package com.edara.edara.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserRequest {

    @JsonProperty("first_name")
    @NotNull(message = "First name must not be null")
    @NotEmpty(message = "First name must not be mpty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "Lirst name must Not Be Null")
    @NotEmpty(message = "Lirst name must Not Be Empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only letters")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;


    @JsonProperty("account")
    @NotNull(message = "Account must not be null")
    @NotEmpty(message = "Account must not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]+@edara\\.com$", message = "Account must follow the format username@edara.com")
    private String account; ;

    @JsonProperty("password")
    @NotNull(message = "Password must not be null")
    @NotEmpty(message = "Password must not be empty")
    @Size(min = 3, max = 50, message = "Password name must be between 3 and 50 characters")
    private String password;


    @JsonProperty("email_address")
    @Email(message = "Invalid email address")
    private String emailAddress;

    @JsonProperty("birthday")
    @JsonFormat(pattern="yyyy-MM-dd")
    @Past(message = "Birthday must be a past date")
    private LocalDate birthday;

    @JsonProperty("image")
    private byte[] image;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("phone_number")
    @Pattern(regexp="(^$|[0-9]{11})" , message = "Invalid phone number")
    private String phoneNumber;

    @JsonProperty("country")
    @Pattern(regexp = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$", message = "Country must contain only letters and spaces")
    private String country;

    @JsonProperty("city")
    @Pattern(regexp = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$", message = "city must contain only letters")
    private String city;

    @JsonProperty("profession")
    @Pattern(regexp = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$", message = "Profession must contain only letters")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String profession;

}
