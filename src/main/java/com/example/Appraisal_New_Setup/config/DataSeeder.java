package com.example.Appraisal_New_Setup.config;

import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.entity.enums.Role;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String hrEmail = "hr@example.com";
        Optional<Employee> existingHr = employeeRepository.findByEmail(hrEmail);
        
        if (existingHr.isEmpty()) {
            Employee hrEmployee = new Employee();
            hrEmployee.setFullName("Human Resources");
            hrEmployee.setEmail(hrEmail);
            hrEmployee.setPassword(passwordEncoder.encode("password123")); // Default password
            hrEmployee.setJobTitle("HR Manager");
            hrEmployee.setRole(Role.HR);
            hrEmployee.setActive(true);
            
            employeeRepository.save(hrEmployee);
            System.out.println("HR Seed User created: hr@example.com / password123");
        } else {
            System.out.println("HR Seed User already exists.");
        }
    }
}
