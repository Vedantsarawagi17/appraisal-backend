package com.example.Appraisal_New_Setup.repository;

import com.example.Appraisal_New_Setup.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Used for Login and fetching own Profile
    Optional<Employee> findByEmail(String email);

    // FUTURE: Used by Managers to see their team
    List<Employee> findByManagerId(Long managerId);

    // Used by HR to see everyone in a Department
    List<Employee> findByDepartmentId(Long deptId);
}