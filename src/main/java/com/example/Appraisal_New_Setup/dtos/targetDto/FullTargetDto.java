package com.example.Appraisal_New_Setup.dtos.targetDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FullTargetDto {
    private Long id;
    private String title;
    private String managerComments;
    private LocalDate dueDate;
    private String employeeComments;

    // Field names match entity getters (isManagerSubmitted() -> managerSubmitted)
    // @JsonProperty ensures the frontend still receives "isManagerSubmitted"
    @JsonProperty("isManagerSubmitted")
    private boolean managerSubmitted;

    @JsonProperty("isEmployeeSubmitted")
    private boolean employeeSubmitted;

    private String employeeFullName;
    private String managerFullName;
}
