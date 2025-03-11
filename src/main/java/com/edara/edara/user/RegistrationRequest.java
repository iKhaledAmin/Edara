package com.edara.edara.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

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

}
