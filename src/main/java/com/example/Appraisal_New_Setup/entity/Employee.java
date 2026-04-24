package com.example.Appraisal_New_Setup.entity;

import com.example.Appraisal_New_Setup.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "job_title")
    private String jobTitle;

    @ManyToOne // Many employees report to one manager
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = true) // Self-referencing relationship
    private Employee manager;

    @ManyToOne // Many employees belong to one department
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = true) // This creates the Foreign Key column
    private Department department;

    @Enumerated(EnumType.STRING)
    private Role role; // EMPLOYEE, MANAGER, HR

    // Removed specific name="is_active" to prevent MySQL from keeping an orphaned "active" NOT NULL column
    private boolean active;
}
