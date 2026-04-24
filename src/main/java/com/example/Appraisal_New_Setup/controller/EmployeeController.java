package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.employeeDto.requestEmployeeDto;
import com.example.Appraisal_New_Setup.dtos.employeeDto.responseEmployeeDto;
import com.example.Appraisal_New_Setup.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<responseEmployeeDto> createEmployee(@RequestBody requestEmployeeDto requestDto) {
        return ResponseEntity.ok(employeeService.createEmployee(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<responseEmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<responseEmployeeDto>> getEmployeesByManagerId(@PathVariable Long managerId) {
        return ResponseEntity.ok(employeeService.getEmployeesByManagerId(managerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<responseEmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<responseEmployeeDto> updateEmployee(
            @PathVariable Long id,
            @RequestBody requestEmployeeDto requestDto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}