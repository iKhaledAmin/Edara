package com.edara.edara.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonProperty("account")
    @NotNull(message = "Account must not be null")
    private String account;

    @JsonProperty("password")
    @NotNull(message = "Password must not be null")
    private String password;
}
