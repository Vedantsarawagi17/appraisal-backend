package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.EmployeeAppraisalDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.FullAppraisalCycleDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.HrCreateAppraisalCycleDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.HrFinalCommentsDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.ManagerAppraisalDto;
import com.example.Appraisal_New_Setup.service.AppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appraisals")
public class AppraisalController {

    @Autowired
    private AppraisalService appraisalService;

//      ----------------- Cycle Crud Operations ---------------------

    @PostMapping("/initiate")
    public ResponseEntity<String> globalInit(@RequestBody HrCreateAppraisalCycleDto dto) {
        appraisalService.initiateGlobalCycle(dto);
        return ResponseEntity.ok("Cycle started! Appraisal forms generated for all employees.");
    }

    @PutMapping("/cycle/{cycleName}")
    public ResponseEntity<String> updateCycle(@PathVariable String cycleName, @RequestBody HrCreateAppraisalCycleDto dto) {
        appraisalService.updateGlobalCycle(cycleName, dto);
        return ResponseEntity.ok("Cycle updated successfully!");
    }

    @PutMapping("/{id}/cycle")
    public ResponseEntity<String> updateCycleById(@PathVariable Long id, @RequestBody HrCreateAppraisalCycleDto dto) {
        appraisalService.updateGlobalCycleById(id, dto);
        return ResponseEntity.ok("Cycle updated successfully!");
    }

    // --- VIEW / GET ENDPOINTS ---
    @GetMapping("/{id}")
    public ResponseEntity<FullAppraisalCycleDto> getAppraisal(@PathVariable Long id) {
        return ResponseEntity.ok(appraisalService.getAppraisalById(id));
    }

    @GetMapping
    public ResponseEntity<List<FullAppraisalCycleDto>> getAllAppraisals() {
        return ResponseEntity.ok(appraisalService.getAllAppraisals());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<FullAppraisalCycleDto>> getAppraisalsByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(appraisalService.getAppraisalsByEmployeeId(employeeId));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<FullAppraisalCycleDto>> getAppraisalsByManagerId(@PathVariable Long managerId) {
        return ResponseEntity.ok(appraisalService.getAppraisalsByManagerId(managerId));
    }

// --- EMPLOYEE ENDPOINTS ---
    @PutMapping("/{id}/employee/save")
    public ResponseEntity<Void> employeeSave(@PathVariable Long id, @RequestBody EmployeeAppraisalDto dto) {
        appraisalService.updateEmployeeAssessment(id, dto);
        return ResponseEntity.ok().build();
    }

//
    @PostMapping("/{id}/employee/submit")
    public ResponseEntity<Void> employeeSubmit(@PathVariable Long id) {
        appraisalService.submitToManager(id);
        return ResponseEntity.ok().build();
    }

//
    // --- MANAGER ENDPOINTS ---
    @PutMapping("/{id}/manager/save")
    public ResponseEntity<Void> managerSave(@PathVariable Long id, @RequestBody ManagerAppraisalDto dto) {
        appraisalService.updateManagerReview(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/manager/approve")
    public ResponseEntity<Void> managerApprove(@PathVariable Long id) {
        appraisalService.approveToHR(id);
        return ResponseEntity.ok().build();
    }

    // --- HR ENDPOINTS ---
    @PostMapping("/{id}/hr/approve-direct")
    public ResponseEntity<Void> hrDirectApprove(@PathVariable Long id) {
        appraisalService.hrDirectApprove(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/hr/save")
    public ResponseEntity<Void> hrSave(@PathVariable Long id, @RequestBody HrFinalCommentsDto dto) {
        appraisalService.updateHrDraft(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/hr/complete")
    public ResponseEntity<String> hrComplete(@PathVariable Long id, @RequestBody HrFinalCommentsDto dto) {
        appraisalService.completeAppraisalCycle(id, dto);
        return ResponseEntity.ok("Appraisal cycle finalized and archived.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppraisal(@PathVariable Long id) {
        appraisalService.deleteAppraisalRecord(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cycle/{cycleName}")
    public ResponseEntity<Void> deleteCycle(@PathVariable String cycleName) {
        appraisalService.deleteCycle(cycleName);
        return ResponseEntity.noContent().build();
    }
}
