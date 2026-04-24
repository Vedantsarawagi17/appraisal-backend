package com.example.Appraisal_New_Setup.dtos.authDto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}