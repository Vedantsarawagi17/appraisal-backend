package com.example.Appraisal_New_Setup.repository;

import com.example.Appraisal_New_Setup.entity.Appraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    // Prevents HR from creating the same cycle twice for one person
    boolean existsByCycleNameAndEmployeeId(String cycleName, Long employeeId);

    // Dynamic Lookup: Find appraisals where the associated employee's current manager matches the ID
    List<Appraisal> findByEmployeeManagerId(Long managerId);

    // Snapshot Lookup: Find appraisals by the manager recorded at cycle creation time
    List<Appraisal> findByManagerId(Long managerId);

    // Used by Employees to see their own appraisal history
    List<Appraisal> findByEmployeeId(Long employeeId);

    // Used to find all appraisals belonging to a specific cycle
    List<Appraisal> findByCycleName(String cycleName);

    // Delete all appraisals for a cycle
    void deleteByCycleName(String cycleName);

    // Delete when employee is hard deleted
    void deleteByEmployeeId(Long employeeId);
    void deleteByManagerId(Long managerId);
}
