package com.example.Appraisal_New_Setup.dtos.reportDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeReportDto {
    private Long id;
    private String cycleName;
    private LocalDate cycleStartDate;
    private LocalDate cycleEndDate;

    private String employeeFullName;
    private String managerFullName;
    private String departmentName;

    // Employee Content
    private String whatWentWell;
    private String whatToImprove;
    private String achievements;

    // Manager Content
    private String managerStrengths;
    private String managerImprovements;
    private String managerComments;
    private Integer managerRating;

    // No HR comments for the employee report!
}
