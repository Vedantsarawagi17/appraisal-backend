package com.example.Appraisal_New_Setup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity  // Mapped to a Database Table
@Table(name = "appraisals")
public class Appraisal {

    @Id // Primary Key of the Database
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Incrementing Integer
    private Long id;

    // --- HR INITIALIZED FIELDS ---
    @Column(name = "cycle_name", nullable = false, length = 150)
    private String cycleName;// FY2026 / FY2025

    @Column(name = "cycle_start_date", nullable = false)
    private LocalDate cycleStartDate;

    @Column(name = "cycle_end_date", nullable = false)
    private LocalDate cycleEndDate;

    // --- EMPLOYEE FIELDS ---
    // Self assessment fields
    @Column(name = "what_went_well", columnDefinition = "TEXT")
    private String whatWentWell;

    @Column(name = "what_to_improve", columnDefinition = "TEXT")
    private String whatToImprove;

    @Column(name = "achievements", columnDefinition = "TEXT")
    private String achievements;

    @Column(name = "is_submitted")
    private boolean isSubmitted;

    // --- MANAGER FIELDS ---
    // Manager review fields
    @Column(name = "manager_strengths", columnDefinition = "TEXT")
    private String managerStrengths;

    @Column(name = "manager_improvements", columnDefinition = "TEXT")
    private String managerImprovements;

    @Column(name = "manager_comments", columnDefinition = "TEXT")
    private String managerComments;

    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "is_approved")
    private boolean isApproved ;

    // --- FINAL HR FIELDS ---
    @Column(name = "hr_comments", columnDefinition = "TEXT")
    private String hrComments;

    @Column(name = "is_completed")
    private boolean isCompleted ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id", nullable = false)
    private Employee employee;

    // A manager can be null because the CEO or the top HR head won't have a manager!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true) 
    private Employee manager;

    @Column(name = "department_name")
    private String departmentName;
}
