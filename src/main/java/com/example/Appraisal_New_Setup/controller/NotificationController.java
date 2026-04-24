package com.example.Appraisal_New_Setup.controller;

import com.example.Appraisal_New_Setup.entity.Notification;
import com.example.Appraisal_New_Setup.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Fetch all notifications for a specific employee (by email)
    @GetMapping("/{employeeEmail}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String employeeEmail) {
        return ResponseEntity.ok(notificationService.getNotificationsForEmployee(employeeEmail));
    }

    // Mark a single notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Get the count of unread notifications for badge
    @GetMapping("/{employeeEmail}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String employeeEmail) {
        long count = notificationService.getUnreadCount(employeeEmail);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
