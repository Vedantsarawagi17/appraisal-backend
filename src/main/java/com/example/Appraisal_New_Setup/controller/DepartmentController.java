package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.dtos.departmentDto.requestDepartmentDto;
import com.example.Appraisal_New_Setup.dtos.departmentDto.responseDepartmentDto;
import com.example.Appraisal_New_Setup.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody requestDepartmentDto requestDto) {
        try {
            return ResponseEntity.ok(departmentService.createDepartment(requestDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<responseDepartmentDto>> getAllDepartment() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
    @GetMapping("/{id}")
    public ResponseEntity<responseDepartmentDto> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<responseDepartmentDto> updateDepartment(
            @PathVariable Long id,
            @RequestBody requestDepartmentDto requestDto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok("Department deleted successfully");
    }
}