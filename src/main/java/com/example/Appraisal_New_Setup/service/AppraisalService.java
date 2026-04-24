package com.example.Appraisal_New_Setup.service;

import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.EmployeeAppraisalDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.FullAppraisalCycleDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.HrCreateAppraisalCycleDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.HrFinalCommentsDto;
import com.example.Appraisal_New_Setup.dtos.appraisalCycleDto.ManagerAppraisalDto;
import com.example.Appraisal_New_Setup.dtos.reportDto.EmployeeReportDto;
import com.example.Appraisal_New_Setup.entity.Appraisal;
import com.example.Appraisal_New_Setup.entity.Employee;
import com.example.Appraisal_New_Setup.repository.AppraisalRepository;
import com.example.Appraisal_New_Setup.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AppraisalService {
    @Autowired
    private AppraisalRepository appraisalRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationService notificationService;

    // --- 1. HR: GLOBAL INITIATION ---
    @Transactional
    public void initiateGlobalCycle(HrCreateAppraisalCycleDto dto) {
        List<Employee> allEmployees = employeeRepository.findAll();
        
        List<Appraisal> newAppraisals = allEmployees.stream()
                // Skip HR users — they manage the cycle, they don't participate in it
                .filter(emp -> emp.getRole() != com.example.Appraisal_New_Setup.entity.enums.Role.HR)
                .filter(emp -> !appraisalRepository.existsByCycleNameAndEmployeeId(dto.getCycleName(), emp.getId()))
                .map(emp -> {
                    Appraisal app = new Appraisal();
                    app.setCycleName(dto.getCycleName());
                    app.setCycleStartDate(dto.getCycleStartDate());
                    app.setCycleEndDate(dto.getCycleEndDate());
                    app.setEmployee(emp);
                    // Manager snapshot — may be null if the employee/manager has no manager above them.
                    // That's fine: the record is still created so they can fill their self-appraisal.
                    // HR will handle finalization directly for manager-less records.
                    app.setManager(emp.getManager());

                    // Capture the Department Snapshot
                    if (emp.getDepartment() != null) {
                        app.setDepartmentName(emp.getDepartment().getName());
                    }
                    return app;
                }).collect(Collectors.toList());

        appraisalRepository.saveAll(newAppraisals);

        // Notify all employees about the new cycle
        List<String> employeeEmails = newAppraisals.stream()
                .map(app -> app.getEmployee().getEmail())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // 1. Send Email
        emailService.sendCycleStartedEmail(employeeEmails, dto.getCycleName());
        
        // 2. Save In-App Notification
        for (String email : employeeEmails) {
            notificationService.sendNotification(
                email, 
                "New Appraisal Cycle Started \uD83D\uDE80", 
                "HR has initiated a new cycle: " + dto.getCycleName()
            );
        }
    }

    @Transactional
    public void updateGlobalCycle(String oldCycleName, HrCreateAppraisalCycleDto dto) {
        List<Appraisal> appraisals = appraisalRepository.findByCycleName(oldCycleName);
        if (appraisals.isEmpty()) {
            throw new RuntimeException("No appraisals found for cycle: " + oldCycleName);
        }

        appraisals.forEach(app -> {
            app.setCycleName(dto.getCycleName());
            app.setCycleStartDate(dto.getCycleStartDate());
            app.setCycleEndDate(dto.getCycleEndDate());
        });

        appraisalRepository.saveAll(appraisals);
    }

    @Transactional
    public void updateGlobalCycleById(Long id, HrCreateAppraisalCycleDto dto) {
        Appraisal representative = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found: " + id));
        
        String oldCycleName = representative.getCycleName();
        List<Appraisal> appraisals = appraisalRepository.findByCycleName(oldCycleName);

        appraisals.forEach(app -> {
            app.setCycleName(dto.getCycleName());
            app.setCycleStartDate(dto.getCycleStartDate());
            app.setCycleEndDate(dto.getCycleEndDate());
        });

        appraisalRepository.saveAll(appraisals);
    }

    // --- 2. EMPLOYEE: SAVE DRAFT & SUBMIT ---
    @Transactional
    public void updateEmployeeAssessment(Long id, EmployeeAppraisalDto dto) {
        Appraisal app = appraisalRepository.findById(id).orElseThrow();
        if (app.isSubmitted()) throw new IllegalStateException("Appraisal is already submitted and locked.");

        app.setWhatWentWell(dto.getWhatWentWell());
        app.setWhatToImprove(dto.getWhatToImprove());
        app.setAchievements(dto.getAchievements());

        appraisalRepository.save(app);
    }

    @Transactional
    public void submitToManager(Long id) {
        Appraisal app = appraisalRepository.findById(id).orElseThrow();
        app.setSubmitted(true); // LOCKS THE EMPLOYEE FIELDS
        
        // Ensure the manager is synced with the employee's current manager if it was null
        if (app.getManager() == null && app.getEmployee().getManager() != null) {
            app.setManager(app.getEmployee().getManager());
        }
        
        appraisalRepository.save(app);

        // Notify the manager
        if (app.getManager() != null && app.getManager().getEmail() != null) {
            // 1. Send Email
            emailService.sendEmployeeSubmittedEmail(
                    app.getManager().getEmail(),
                    app.getManager().getFullName(),
                    app.getEmployee().getFullName()
            );
            
            // 2. Save In-App Notification
            notificationService.sendNotification(
                app.getManager().getEmail(), 
                "Self-Review Submitted \uD83D\uDCCB", 
                app.getEmployee().getFullName() + " has submitted their self-review."
            );
        }
    }

    // --- 3. MANAGER: REVIEW & APPROVE ---
    @Transactional
    public void updateManagerReview(Long id, ManagerAppraisalDto dto) {
        Appraisal app = appraisalRepository.findById(id).orElseThrow();
        if (!app.isSubmitted()) throw new IllegalStateException("Employee hasn't submitted yet.");
        if (app.isApproved()) throw new IllegalStateException("Already approved and sent to HR.");

        app.setManagerStrengths(dto.getManagerStrengths());
        app.setManagerImprovements(dto.getManagerImprovements());
        app.setManagerComments(dto.getManagerComments());
        if (dto.getManagerRating() != null) {
            app.setManagerRating(dto.getManagerRating());
        }

        appraisalRepository.save(app);
    }

    @Transactional
    public void approveToHR(Long id) {
        Appraisal app = appraisalRepository.findById(id).orElseThrow();
        app.setApproved(true); // LOCKS THE MANAGER FIELDS
        appraisalRepository.save(app);

        // Notify the employee that their manager has completed the review
        if (app.getEmployee() != null && app.getEmployee().getEmail() != null) {
            // 1. Send Email
            emailService.sendManagerApprovedEmail(
                    app.getEmployee().getEmail(),
                    app.getEmployee().getFullName(),
                    app.getManager() != null ? app.getManager().getFullName() : "Your Manager"
            );
            
            // 2. Save In-App Notification
            notificationService.sendNotification(
                app.getEmployee().getEmail(), 
                "Manager Review Complete \uD83C\uDF89", 
                "Your manager has finalized and approved your performance review."
            );
        }
    }

    // --- 4. HR: REVIEW & COMPLETE ---
    // HR can directly approve records where no manager was assigned (top-level employees/managers)
    @Transactional
    public void hrDirectApprove(Long id) {
        Appraisal app = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found"));
        if (app.getManager() != null) {
            throw new IllegalStateException("This appraisal has a manager assigned. It must go through the normal manager approval flow.");
        }
        if (!app.isSubmitted()) {
            throw new IllegalStateException("Employee must submit their self-appraisal first.");
        }
        app.setApproved(true);
        appraisalRepository.save(app);
    }

    @Transactional
    public void updateHrDraft(Long id, HrFinalCommentsDto dto) {
        Appraisal app = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found"));

        if (!app.isApproved()) {
            throw new IllegalStateException("Manager must approve before HR can edit.");
        }
        if (app.isCompleted()) {
            throw new IllegalStateException("Appraisal cycle is already completed and locked.");
        }

        app.setHrComments(dto.getHrComments());
        appraisalRepository.save(app);
    }

    @Transactional
    public void completeAppraisalCycle(Long id, HrFinalCommentsDto dto) {
        Appraisal app = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found"));

        // SAFETY CHECK: HR can only complete if the Manager has already approved
        if (!app.isApproved()) {
            throw new IllegalStateException("Manager must approve before HR can complete the cycle.");
        }

        // Save the final comments
        app.setHrComments(dto.getHrComments());

        // Flip the 'isCompleted' switch
        app.setCompleted(true);
        appraisalRepository.save(app);
    }

    // --- 5. VIEWING / FETCHING ---
    @Transactional(readOnly = true)
    public FullAppraisalCycleDto getAppraisalById(Long id) {
        Appraisal app = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found"));
        return mapToDto(app);
    }

    @Transactional(readOnly = true)
    public List<FullAppraisalCycleDto> getAllAppraisals() {
        return appraisalRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FullAppraisalCycleDto> getAppraisalsByEmployeeId(Long employeeId) {
        return appraisalRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<FullAppraisalCycleDto> getAppraisalsByManagerId(Long managerId) {
        // Use the manager snapshot stored on the appraisal record (set at cycle creation).
        // This ensures a manager always sees the appraisals they were assigned to,
        // even if the employee is later reassigned to a different manager.
        return appraisalRepository.findByManagerId(managerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAppraisalRecord(Long id) {
        appraisalRepository.deleteById(id);
    }

    @Transactional
    public void deleteCycle(String cycleName) {
        appraisalRepository.deleteByCycleName(cycleName);
    }

    private FullAppraisalCycleDto mapToDto(Appraisal app) {
        FullAppraisalCycleDto dto = modelMapper.map(app, FullAppraisalCycleDto.class);
        if (app.getEmployee() != null) {
            dto.setEmployeeFullName(app.getEmployee().getFullName());
            if (app.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(app.getEmployee().getDepartment().getName());
            }
        }
        if (app.getManager() != null) {
            dto.setManagerFullName(app.getManager().getFullName());
        } else if (app.getEmployee() != null && app.getEmployee().getManager() != null) {
            // Fallback to current manager if snapshot is empty
            dto.setManagerFullName(app.getEmployee().getManager().getFullName());
        }
        return dto;
    }

    // Report specific for Employee (hides HR comments)
    public EmployeeReportDto getEmployeeReport(Long id) {
        Appraisal app = appraisalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appraisal record not found"));
        EmployeeReportDto dto = modelMapper.map(app, EmployeeReportDto.class);
        
        // Manual mapping parity with mapToDto
        if (app.getEmployee() != null) {
            dto.setEmployeeFullName(app.getEmployee().getFullName());
            if (app.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(app.getEmployee().getDepartment().getName());
            }
        }
        
        if (app.getManager() != null) {
            dto.setManagerFullName(app.getManager().getFullName());
        } else if (app.getEmployee() != null && app.getEmployee().getManager() != null) {
            dto.setManagerFullName(app.getEmployee().getManager().getFullName());
        }
        
        return dto;
    }
}
