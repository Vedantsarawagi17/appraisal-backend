package com.example.Appraisal_New_Setup.repository;

import com.example.Appraisal_New_Setup.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Long> {
    // Employee sees ALL targets assigned to them (manager submitted or not)
    List<Target> findByEmployeeId(Long employeeId);

    // Keep this for any future filtered use
    List<Target> findByEmployeeIdAndManagerSubmittedTrue(Long employeeId);

    // Manager sees EVERYTHING they created (Drafts + Submitted) 
    // OR any targets assigned to their reports by others
    List<Target> findByManagerIdOrEmployeeManagerId(Long managerId, Long reportingManagerId);

    void deleteByEmployeeId(Long employeeId);
    void deleteByManagerId(Long managerId);
}

//Target entity structure =>
//id , title , managerComments,