package com.edara.edara.service;

import com.edara.edara.model.entity.Employee;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.enums.EmployeeType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface EmployeeService {

    Employee save(Employee employee);
    Employee add(Member newMember, EmployeeType employeeType, Double baseSalary,Double bonusSalary);
    Employee updateEntity(Long employeeId, Employee newEmployee);


    Optional<Employee> getEntityById(Long employeeId);
    Employee getById(Long employeeId);



}
