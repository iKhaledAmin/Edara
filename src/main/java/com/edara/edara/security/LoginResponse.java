package com.edara.edara.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
@Data
public class LoginResponse {
    private Long id;
    private String userName;
    private String jwtToken;
    private Collection<? extends GrantedAuthority> authorities;
}
