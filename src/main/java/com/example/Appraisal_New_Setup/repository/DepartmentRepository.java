package com.example.Appraisal_New_Setup.repository;

import com.example.Appraisal_New_Setup.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
}
