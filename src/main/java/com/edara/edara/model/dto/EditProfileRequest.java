package com.edara.edara.model.dto;

import com.edara.edara.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest extends UserRequest {
    @JsonProperty("role")
    @NotNull(message = "Role must not be mull")
    private Role role;
}
