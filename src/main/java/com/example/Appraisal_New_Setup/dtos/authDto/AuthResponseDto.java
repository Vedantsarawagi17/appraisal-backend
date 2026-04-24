package com.example.Appraisal_New_Setup.dtos.authDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String role;
    private Long employeeId;
}