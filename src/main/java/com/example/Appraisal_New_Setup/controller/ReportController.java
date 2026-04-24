package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.FullAppraisalCycleDto;
import com.example.Appraisal_New_Setup.dtos.reportDto.EmployeeReportDto;
import com.example.Appraisal_New_Setup.service.AppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private AppraisalService appraisalService;

    // 1. Employee Report (Does NOT contain HR comments)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')") // Managers are employees too!
    @GetMapping("/{id}/employee")
    public ResponseEntity<EmployeeReportDto> getEmployeeReport(@PathVariable Long id) {
        return ResponseEntity.ok(appraisalService.getEmployeeReport(id));
    }

    // 2. Full Report for Manager and HR (Contains EVERYTHING, including HR comments)
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    @GetMapping("/{id}/full")
    public ResponseEntity<FullAppraisalCycleDto> getFullReport(@PathVariable Long id) {
        // We can just reuse your existing FullAppraisalCycleDto since it has everything!
        return ResponseEntity.ok(appraisalService.getAppraisalById(id));
    }
}
