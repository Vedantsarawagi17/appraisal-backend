package com.example.Appraisal_New_Setup.service;

import com.example.Appraisal_New_Setup.entity.Notification;
import com.example.Appraisal_New_Setup.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(String employeeEmail, String title, String message) {
        if (employeeEmail == null) return;

        Notification notification = new Notification();
        notification.setEmployee(employeeEmail);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForEmployee(String employeeEmail) {
        return notificationRepository.findByEmployeeOrderByIdDesc(employeeEmail);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public long getUnreadCount(String employeeEmail) {
        return notificationRepository.countByEmployeeAndRead(employeeEmail, false);
    }
}
