package com.edara.edara.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonProperty("user_name")
    @NotNull(message = "Username must not be null")
    private String userName;

    @JsonProperty("password")
    @NotNull(message = "Password must not be null")
    private String password;
}
