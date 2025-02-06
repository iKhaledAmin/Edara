package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.EmployeeResponse;
import com.edara.edara.model.dto.MemberRequest;
import com.edara.edara.model.dto.MemberResponse;
import com.edara.edara.model.entity.Employee;
import com.edara.edara.model.entity.Member;
import com.edara.edara.model.enums.MemberType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    // Initially ignore employee mapping
    @Mapping(target = "employee", ignore = true)
    Member toEntity(MemberRequest request);

    // After the main mapping, use @AfterMapping to handle conditional logic
    @AfterMapping
    default void mapEmployeeDetails(MemberRequest request, @MappingTarget Member member) {
        if (request.getMemberType() == MemberType.EMPLOYEE_MEMBER && request.getEmployeeRequest() != null) {
            // Ensure Employee is initialized
            Employee employee = new Employee();

            employee.setBaseSalary(request.getEmployeeRequest().getBaseSalary());
            employee.setBonusSalary(request.getEmployeeRequest().getBonusSalary());
            employee.setType(request.getEmployeeRequest().getType());

            member.setEmployee(employee); // Set employee details
        }
    }

    @Mapping(target = "memberCode", source = "entity.user.userCode")
    @Mapping(target = "memberName", expression = "java(concatenateUserName(entity.getUser().getFirstName(), entity.getUser().getLastName()))") // Call the method
    @Mapping(target = "memberImage", source = "entity.user.image")
    @Mapping(target = "joinDate", source = "entity.createdAt")
    @Mapping(target = "title", source = "entity.title.name")
    @Mapping(target = "employeeDetails", expression = "java(getEmployeeDetails(entity))") // Call the method
    MemberResponse toResponse(Member entity);

    default EmployeeResponse getEmployeeDetails(Member member) {
        if (member.getMemberType() == MemberType.NORMAL_MEMBER) {
            return null; // Return null if NORMAL_MEMBER
        }
        Employee employee = member.getEmployee();
        if (employee == null) {
            return null; // Return null if there's no employee record
        }
        EmployeeResponse employeeResponse = new EmployeeResponse();

        employeeResponse.setEmployeeType(employee.getType());
        employeeResponse.setBaseSalary(employee.getBaseSalary());
        employeeResponse.setBonusSalary(employee.getBonusSalary());
        employeeResponse.setTotalSalary(employee.getBaseSalary() + employee.getBonusSalary());

        return employeeResponse;
    }
    default String concatenateUserName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }


}
