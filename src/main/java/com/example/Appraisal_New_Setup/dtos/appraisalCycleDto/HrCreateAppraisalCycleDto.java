package com.example.Appraisal_New_Setup.dtos.appraisalCycleDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HrCreateAppraisalCycleDto {
    private String cycleName;
    private LocalDate cycleStartDate;
    private LocalDate cycleEndDate;
}