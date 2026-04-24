package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.targetDto.EmployeeTargetDto;
import com.example.Appraisal_New_Setup.dtos.targetDto.FullTargetDto;
import com.example.Appraisal_New_Setup.dtos.targetDto.ManagerTargetDto;
import com.example.Appraisal_New_Setup.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/targets")
public class TargetController {
    @Autowired
    private TargetService targetService;

    // --- GET / VIEW ENDPOINTS ---
    @GetMapping("/{id}")
    public ResponseEntity<FullTargetDto> getTargetById(@PathVariable Long id) {
        return ResponseEntity.ok(targetService.getTargetById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<java.util.List<FullTargetDto>> getTargetsForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(targetService.getTargetsForEmployee(employeeId));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<java.util.List<FullTargetDto>> getTargetsForManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(targetService.getTargetsForManager(managerId));
    }

    // Manager Action: Create Target (can be Draft or Submitted)
    @PostMapping("/manager/{managerId}/create")
    public ResponseEntity<Void> createTarget(@PathVariable Long managerId, @RequestBody ManagerTargetDto dto) {
        targetService.saveTarget(managerId, dto);
        return ResponseEntity.ok().build();
    }

    // Manager Action: Edit Target
    @PutMapping("/{id}/manager/edit")
    public ResponseEntity<Void> updateTarget(@PathVariable Long id, @RequestBody ManagerTargetDto dto) {
        targetService.updateTarget(id, dto);
        return ResponseEntity.ok().build();
    }

    // Manager Action: Delete Target
    @DeleteMapping("/{id}/manager/delete")
    public ResponseEntity<Void> deleteTarget(@PathVariable Long id) {
        targetService.deleteTarget(id);
        return ResponseEntity.noContent().build();
    }

    // Manager Action: Submit (Lock)
    @PostMapping("/{id}/manager/submit")
    public ResponseEntity<Void> managerSubmit(@PathVariable Long id) {
        targetService.submitTargetByManager(id);
        return ResponseEntity.ok().build();
    }

    // Employee Action: Add Comments
    @PutMapping("/{id}/employee/feedback")
    public ResponseEntity<Void> employeeFeedback(@PathVariable Long id, @RequestBody EmployeeTargetDto dto) {
        targetService.updateEmployeeFeedback(id, dto);
        return ResponseEntity.ok().build();
    }

    // Employee Action: Submit (Final Lock)
    @PostMapping("/{id}/employee/submit")
    public ResponseEntity<Void> employeeSubmit(@PathVariable Long id) {
        targetService.submitTargetByEmployee(id);
        return ResponseEntity.ok().build();
    }
}
