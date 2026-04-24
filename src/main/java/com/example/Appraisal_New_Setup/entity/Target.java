package com.example.Appraisal_New_Setup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate; // Import for LocalDate

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "targets")
public class Target {
    @Id // Primary Key of the Database
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Incrementing Integer
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String managerComments;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String employeeComments;

    @Column(name = "is_manager_submitted") // Manager locks the target
    private boolean managerSubmitted ; // False = can edit, True = locked

    @Column(name = "is_employee_submitted") // Employee acknowledges
    private boolean employeeSubmitted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;
}
