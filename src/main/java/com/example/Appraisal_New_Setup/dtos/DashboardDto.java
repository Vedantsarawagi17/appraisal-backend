package com.example.Appraisal_New_Setup.dtos;

import lombok.Data;

@Data
public class DashboardDto {
    private String fullName;
    private String email;
    private String jobTitle;
    private String departmentName; // Automatically maps from employee.department.name
    private String managerFullName; // Automatically maps from employee.manager.fullName
}