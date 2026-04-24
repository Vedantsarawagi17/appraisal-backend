package com.example.Appraisal_New_Setup.dtos.employeeDto;

import com.example.Appraisal_New_Setup.entity.enums.Role;
import lombok.Data;

@Data
public class requestEmployeeDto {
    private String fullName;
    private String email;
    private String password;
    private String jobTitle;
    private Role role;
    private Long departmentId; // ID reference for mapping
    private Long managerId;    // ID reference for mapping
    private boolean active = true;
}
