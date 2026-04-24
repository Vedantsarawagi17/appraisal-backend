package com.example.Appraisal_New_Setup.dtos.targetDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagerTargetDto {
    private String title;
    private String managerComments;
    private LocalDate dueDate;
    private Long employeeId; // To link the target to a specific subordinate

    // Use non-boolean-prefixed field name to avoid Lombok is-prefix getter/setter mismatch
    // Jackson will correctly deserialize "submitted": true OR "isSubmitted": true
    @JsonProperty("isSubmitted")
    private boolean submitted; // Whether to send immediately or save as draft
}
