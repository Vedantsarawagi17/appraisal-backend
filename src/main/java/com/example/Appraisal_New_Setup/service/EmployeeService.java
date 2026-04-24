package com.example.Appraisal_New_Setup.service;

import com.example.Appraisal_New_Setup.dtos.employeeDto.requestEmployeeDto;
import com.example.Appraisal_New_Setup.dtos.employeeDto.responseEmployeeDto;
import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.repository.AppraisalRepository;
import com.example.Appraisal_New_Setup.repository.DepartmentRepository;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import com.example.Appraisal_New_Setup.repository.NotificationRepository;
import com.example.Appraisal_New_Setup.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AppraisalRepository appraisalRepository;
    @Autowired
    private TargetRepository targetRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Helper to manually link Department and Manager entities based on IDs in DTO
     */
    private void resolveDependencies(requestEmployeeDto dto, Employee employee) {
        // If departmentId is null, the employee belongs to no department (Default NULL)
        employee.setDepartment(dto.getDepartmentId() != null
                ? departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"))
                : null);

        // If managerId is null, the employee has no manager (Default NULL - e.g. CEO)
        employee.setManager(dto.getManagerId() != null
                ? employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"))
                : null);
    }

    private responseEmployeeDto mapToDto(Employee emp) {
        responseEmployeeDto dto = modelMapper.map(emp, responseEmployeeDto.class);
        if (emp.getDepartment() != null) {
            dto.setDepartmentName(emp.getDepartment().getName());
            dto.setDepartmentId(emp.getDepartment().getId());
        }
        if (emp.getManager() != null) {
            dto.setManagerFullName(emp.getManager().getFullName());
            dto.setManagerId(emp.getManager().getId());
        }
        return dto;
    }

    public responseEmployeeDto createEmployee(requestEmployeeDto requestDto) {
        Employee employee = new Employee();
        employee.setFullName(requestDto.getFullName());
        employee.setEmail(requestDto.getEmail());
        employee.setJobTitle(requestDto.getJobTitle());
        employee.setRole(requestDto.getRole());
        employee.setActive(requestDto.isActive());
        
        // IMPORTANT: Hash the password before saving to the database!
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        } else {
            // Failsafe default password if frontend validation gets bypassed
            employee.setPassword(passwordEncoder.encode("Password123!"));
        }
        
        resolveDependencies(requestDto, employee);

        Employee saved = employeeRepository.save(employee);
        return mapToDto(saved);
    }

    public List<responseEmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<responseEmployeeDto> getEmployeesByManagerId(Long managerId) {
        return employeeRepository.findByManagerId(managerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public responseEmployeeDto getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToDto(emp);
    }

    public responseEmployeeDto updateEmployee(Long id, requestEmployeeDto requestDto) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Update basic fields manually to prevent ModelMapper identity confusion bugs
        existingEmployee.setFullName(requestDto.getFullName());
        existingEmployee.setEmail(requestDto.getEmail());
        existingEmployee.setJobTitle(requestDto.getJobTitle());
        existingEmployee.setRole(requestDto.getRole());
        existingEmployee.setActive(requestDto.isActive());

        // Update password if a new one was provided
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            existingEmployee.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        } else if (existingEmployee.getPassword() == null || existingEmployee.getPassword().isEmpty()) {
            // Defensive failsafe for historical records with corrupted or missing passwords
            existingEmployee.setPassword(passwordEncoder.encode("Password123!"));
        }

        // Update relationships
        resolveDependencies(requestDto, existingEmployee);

        Employee updated = employeeRepository.save(existingEmployee);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // 1. Orphan team members (set their manager to null)
        List<Employee> teamMembers = employeeRepository.findByManagerId(id);
        for (Employee member : teamMembers) {
            member.setManager(null);
            employeeRepository.save(member);
        }

        // 2. Delete related Appraisals (where this person is either the subject or the manager)
        appraisalRepository.deleteByEmployeeId(id);
        appraisalRepository.deleteByManagerId(id);

        // 3. Delete related Targets (where this person is either the subject or the manager)
        targetRepository.deleteByEmployeeId(id);
        targetRepository.deleteByManagerId(id);

        // 4. Delete Notifications (using email as key)
        notificationRepository.deleteByEmployee(emp.getEmail());

        // 5. Finally, hard delete the employee from the database
        employeeRepository.deleteById(id);
    }
}
