package com.example.Appraisal_New_Setup.service;


import com.example.Appraisal_New_Setup.dtos.departmentDto.requestDepartmentDto;
import com.example.Appraisal_New_Setup.dtos.departmentDto.responseDepartmentDto;
import com.example.Appraisal_New_Setup.entity.Department;
import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.repository.DepartmentRepository;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public responseDepartmentDto createDepartment(requestDepartmentDto requestDto) {
        if (departmentRepository.findByName(requestDto.getName()).isPresent()) {
            throw new IllegalArgumentException("A department with the name '" + requestDto.getName() + "' already exists.");
        }
        Department department = modelMapper.map(requestDto, Department.class);
        Department savedDepartment = departmentRepository.save(department);
        return modelMapper.map(savedDepartment, responseDepartmentDto.class);
    }

    public List<responseDepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(dept -> modelMapper.map(dept, responseDepartmentDto.class))
                .collect(Collectors.toList());
    }

    public responseDepartmentDto getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return modelMapper.map(dept, responseDepartmentDto.class);
    }

    public responseDepartmentDto updateDepartment(Long id, requestDepartmentDto requestDto) {
        Department existingDept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        modelMapper.map(requestDto, existingDept);
        Department updatedDept = departmentRepository.save(existingDept);
        return modelMapper.map(updatedDept, responseDepartmentDto.class);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Nullify department reference on all employees before deleting
        List<Employee> employees = employeeRepository.findByDepartmentId(id);
        employees.forEach(e -> e.setDepartment(null));
        employeeRepository.saveAll(employees);

        departmentRepository.delete(dept);
    }
}
