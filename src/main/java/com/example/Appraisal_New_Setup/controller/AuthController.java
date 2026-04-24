package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.authDto.AuthRequestDto;
import com.example.Appraisal_New_Setup.dtos.authDto.AuthResponseDto;
import com.example.Appraisal_New_Setup.dtos.authDto.RegisterRequestDto;
import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import com.example.Appraisal_New_Setup.security.CustomUserDetails;
import com.example.Appraisal_New_Setup.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- 1. LOGIN API ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto request) {
        try {
            // Authenticates against the CustomUserDetailsService
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Cast to our CustomUserDetails to access the underlying Employee object
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Generate the token
            String jwtToken = jwtService.generateToken(userDetails);

            // Return token and basic info so the Frontend knows who just logged in
            return ResponseEntity.ok(new AuthResponseDto(
                    jwtToken,
                    userDetails.getEmployee().getRole().name(),
                    userDetails.getEmployee().getId()
            ));
        } catch (BadCredentialsException e) {
            // If the password or email is wrong, return a clean 401 Unauthorized!
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Account disabled or user not found!");
        }
    }

    // --- 2. REGISTER API (For creating new users) ---
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        // Check if user already exists
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new Employee
        Employee newEmployee = new Employee();
        newEmployee.setFullName(request.getFullName());
        newEmployee.setEmail(request.getEmail());
        
        // IMPORTANT: We must hash the password before saving!
        newEmployee.setPassword(passwordEncoder.encode(request.getPassword()));
        
        newEmployee.setJobTitle(request.getJobTitle());
        newEmployee.setRole(request.getRole()); // Passes EMPLOYEE, MANAGER, or HR
        newEmployee.setActive(true);

        employeeRepository.save(newEmployee);

        return ResponseEntity.ok("User registered successfully!");
    }
}
