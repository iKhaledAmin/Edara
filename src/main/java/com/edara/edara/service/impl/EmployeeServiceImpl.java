package com.edara.edara.service.impl;

import com.edara.edara.model.entity.Employee;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.enums.EmployeeType;
import com.edara.edara.repository.EmployeeRepo;
import com.edara.edara.service.EmployeeService;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final NonNullBeanUtils nonNullBeanUtils;

    private Employee create(){
        return new Employee();
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public Employee add(Member member, EmployeeType employeeType, Double baseSalary, Double bonusSalary) {
        if (member == null || employeeType == null || baseSalary == null || bonusSalary == null) {
            return null;
        }

        Employee employee = create();
        employee.setMember(member);
        employee.setType(employeeType);
        employee.setBaseSalary(baseSalary);
        employee.setBonusSalary(bonusSalary);
        employee.setTotalSalary(baseSalary + bonusSalary);

        member.setEmployee(employee);
        return save(employee);
    }

    private Double calculateTotalSalary(Employee employee) {
        if (employee == null) return 0.0;
        return employee.getBonusSalary() + employee.getBaseSalary();
    }

    public Employee updateEntity(Long employeeId, Employee newEmployee) {
        if (employeeId == null || newEmployee == null)
            return null;

        Employee existedEmployee = getById(employeeId);

        // Copy properties from newEmployee to existedEmployee, excluding the "id", "type", "member"
        nonNullBeanUtils.copyProperties(newEmployee, existedEmployee, "id", "type", "member");
        existedEmployee.setTotalSalary(
                calculateTotalSalary(existedEmployee)
        );

        // Save the updated employee
        existedEmployee = save(existedEmployee);

        return existedEmployee;
    }


    @Override
    public Optional<Employee> getEntityById(Long employeeId) {
        return employeeRepo.findById(employeeId);
    }

    @Override
    public Employee getById(Long employeeId) {
        return getEntityById(employeeId).orElseThrow(
                () -> new NoSuchElementException("There is no employee with id  = " + employeeId)
        );
    }

}
