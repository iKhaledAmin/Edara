package com.edara.edara.repository;

import com.edara.edara.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Optional<Employee> findByMember_User_UserCodeAndMember_Project_Id(String userCode, Long projectId);

    Optional<Employee> findByMember_Id(Long memberId);
}
