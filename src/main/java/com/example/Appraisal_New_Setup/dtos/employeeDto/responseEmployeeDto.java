package com.example.Appraisal_New_Setup.dtos.employeeDto;

import com.example.Appraisal_New_Setup.entity.enums.Role;
import lombok.Data;

@Data
public class responseEmployeeDto {
    private Long id;
    private String fullName;
    private String email;
    private String jobTitle;
    private Role role;
    private boolean active;

    // Flattened fields
    private Long departmentId;
    private String departmentName;

    private Long managerId;
    private String managerFullName;
}
