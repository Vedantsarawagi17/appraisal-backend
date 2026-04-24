package com.example.Appraisal_New_Setup.service;

import com.example.Appraisal_New_Setup.dtos.targetDto.EmployeeTargetDto;
import com.example.Appraisal_New_Setup.dtos.targetDto.FullTargetDto;
import com.example.Appraisal_New_Setup.dtos.targetDto.ManagerTargetDto;
import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.entity.Target;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import com.example.Appraisal_New_Setup.repository.TargetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetService {
    @Autowired
    private TargetRepository targetRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;

    // --- VIEW / GET METHOD ---
    private FullTargetDto toFullTargetDto(Target t) {
        FullTargetDto dto = modelMapper.map(t, FullTargetDto.class);
        // ModelMapper cannot flatten nested entity fields automatically,
        // so we set them manually to avoid null employeeFullName / managerFullName
        if (t.getEmployee() != null) dto.setEmployeeFullName(t.getEmployee().getFullName());
        if (t.getManager() != null) dto.setManagerFullName(t.getManager().getFullName());
        // Explicitly set booleans to guard against any ModelMapper is-prefix stripping edge cases
        dto.setManagerSubmitted(t.isManagerSubmitted());
        dto.setEmployeeSubmitted(t.isEmployeeSubmitted());
        return dto;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public FullTargetDto getTargetById(Long id) {
        Target target = targetRepository.findById(id).orElseThrow();
        return toFullTargetDto(target);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<FullTargetDto> getTargetsForEmployee(Long employeeId) {
        return targetRepository.findByEmployeeId(employeeId).stream()
                .map(this::toFullTargetDto)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<FullTargetDto> getTargetsForManager(Long managerId) {
        return targetRepository.findByManagerIdOrEmployeeManagerId(managerId, managerId).stream()
                .map(this::toFullTargetDto)
                .toList();
    }

    // 1. Manager Creates/Saves Target (can be Draft or Submitted)
    @org.springframework.transaction.annotation.Transactional
    public void saveTarget(Long managerId, ManagerTargetDto dto) {
        Employee manager = employeeRepository.findById(managerId).orElseThrow();
        Employee employee = employeeRepository.findById(dto.getEmployeeId()).orElseThrow();

        Target target = new Target();
        // Manually map to avoid ModelMapper id collision bugs
        target.setTitle(dto.getTitle());
        target.setManagerComments(dto.getManagerComments());
        target.setDueDate(dto.getDueDate());
        
        target.setManager(manager);
        target.setEmployee(employee);
        target.setManagerSubmitted(dto.isSubmitted()); // Support immediate submission

        targetRepository.save(target);
    }

    // 2. Manager Submits (Locks for Manager, Opens for Employee)
    @org.springframework.transaction.annotation.Transactional
    public void submitTargetByManager(Long targetId) {
        Target target = targetRepository.findById(targetId).orElseThrow();
        target.setManagerSubmitted(true);
        targetRepository.save(target);
    }

    // 3. Manager Edits Target (only if employee hasn't submitted yet)
    @org.springframework.transaction.annotation.Transactional
    public void updateTarget(Long targetId, ManagerTargetDto dto) {
        Target target = targetRepository.findById(targetId).orElseThrow();
        if (target.isEmployeeSubmitted()) {
            throw new IllegalStateException("Cannot edit a target that has already been completed by the employee.");
        }
        target.setTitle(dto.getTitle());
        target.setManagerComments(dto.getManagerComments());
        target.setDueDate(dto.getDueDate());
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId()).orElseThrow();
            target.setEmployee(employee);
        }
        targetRepository.save(target);
    }

    // 4. Manager Deletes Target (only if employee hasn't submitted yet)
    @org.springframework.transaction.annotation.Transactional
    public void deleteTarget(Long targetId) {
        Target target = targetRepository.findById(targetId).orElseThrow();
        if (target.isEmployeeSubmitted()) {
            throw new IllegalStateException("Cannot delete a target that has already been completed by the employee.");
        }
        targetRepository.delete(target);
    }

    // 3. Employee Adds Comments (no restrictions — any assigned target can be commented on)
    @org.springframework.transaction.annotation.Transactional
    public void updateEmployeeFeedback(Long targetId, EmployeeTargetDto dto) {
        Target target = targetRepository.findById(targetId).orElseThrow();

        if (target.isEmployeeSubmitted()) {
            throw new IllegalStateException("Target feedback is already submitted and locked.");
        }

        target.setEmployeeComments(dto.getEmployeeComments());
        targetRepository.save(target);
    }

    // 4. Employee Final Submission
    @org.springframework.transaction.annotation.Transactional
    public void submitTargetByEmployee(Long targetId) {
        Target target = targetRepository.findById(targetId).orElseThrow(() -> new RuntimeException("Target " + targetId + " not found."));
        target.setEmployeeSubmitted(true);
        targetRepository.save(target);
    }
}
