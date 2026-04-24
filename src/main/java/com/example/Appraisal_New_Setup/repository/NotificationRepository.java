package com.example.Appraisal_New_Setup.repository;

import com.example.Appraisal_New_Setup.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find all notifications for a specific employee (usually email)
    List<Notification> findByEmployeeOrderByIdDesc(String employee);

    // Count unread notifications for badge
    long countByEmployeeAndRead(String employee, boolean read);

    void deleteByEmployee(String employee);
}
