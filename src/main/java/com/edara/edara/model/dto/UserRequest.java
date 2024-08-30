package com.edara.edara.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserRequest extends PersonRequest{

    @JsonProperty("profession")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Profession must contain only letters")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String profession;

}