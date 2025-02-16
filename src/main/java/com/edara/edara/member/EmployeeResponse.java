package com.edara.edara.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

    @Enumerated(EnumType.STRING)
    @JsonProperty("employee_type")
    private EmployeeType employeeType;

    @JsonProperty("base_salary")
    private Double baseSalary;

    @JsonProperty("bonus_salary")
    private Double bonusSalary;

    @JsonProperty("total_salary")
    private Double totalSalary;


}
