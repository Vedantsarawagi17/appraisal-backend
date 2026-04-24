package com.example.Appraisal_New_Setup.dtos.authDto;

import com.example.Appraisal_New_Setup.entity.enums.Role;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
    private String jobTitle;
    private Role role; // HR, MANAGER, EMPLOYEE
}
