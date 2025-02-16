package com.edara.edara.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

    @JsonProperty("employee_type")
    @NotNull(message = "Employee type must not be null")
    private EmployeeType type;

    @JsonProperty("base_salary")
    @NotNull(message = "Base salary must not be null")
    @PositiveOrZero(message = "Base salary must be positive or zero")
    @Digits(integer = 10, fraction = 3, message = "Base salary must be a valid number") // Ensures only numbers are allowed
    private Double baseSalary;


    @JsonProperty("bonus_salary")
    @NotNull(message = "Bonus salary must not be null")
    @PositiveOrZero(message = "Bonus salary must be positive or zero")
    @Digits(integer = 10, fraction = 3, message = "Bonus salary must be a valid number") // Ensures only numbers are allowed
    private Double bonusSalary;

}
