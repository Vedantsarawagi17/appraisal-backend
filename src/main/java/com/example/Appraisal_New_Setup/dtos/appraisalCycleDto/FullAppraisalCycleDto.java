package com.example.Appraisal_New_Setup.dtos.appraisalCycleDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FullAppraisalCycleDto {
    private Long id;
    private String cycleName;
    private LocalDate cycleStartDate;
    private LocalDate cycleEndDate;

    // Renamed to avoid Lombok is-prefix getter/setter mismatch with Jackson
    // @JsonProperty ensures frontend still receives "isSubmitted" etc.
    @JsonProperty("isSubmitted")
    private boolean submitted;

    @JsonProperty("isApproved")
    private boolean approved;

    @JsonProperty("isCompleted")
    private boolean completed;

    // --- DATA FIELDS ---
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

    // HR Content
    private String hrComments;
}
