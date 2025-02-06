package com.edara.edara.model.dto;

import com.edara.edara.model.enums.EmployeeType;
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
