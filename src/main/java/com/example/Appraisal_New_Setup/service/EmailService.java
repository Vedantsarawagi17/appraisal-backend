package com.example.Appraisal_New_Setup.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@company.com}")
    private String fromEmail;

    // 1) HR creates a Cycle => Employee gets an email
    @Async // Runs in a background thread so it doesn't block the API response
    public void sendCycleStartedEmail(List<String> employeeEmails, String cycleName) {
        if (employeeEmails == null || employeeEmails.isEmpty()) return;

        try {
            Context context = new Context();
            context.setVariable("cycleName", cycleName);
            String htmlContent = templateEngine.process("cycle-started", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employeeEmails.toArray(new String[0])); // BCC or TO depending on privacy
            helper.setSubject("New Appraisal Cycle Started: " + cycleName);
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 2) Employee submits selfReview => Manager gets an email
    @Async
    public void sendEmployeeSubmittedEmail(String managerEmail, String managerName, String employeeName) {
        if (managerEmail == null) return;

        try {
            Context context = new Context();
            context.setVariable("managerName", managerName);
            context.setVariable("employeeName", employeeName);
            String htmlContent = templateEngine.process("employee-submitted", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(managerEmail);
            helper.setSubject("Action Required: " + employeeName + " has submitted their Self-Review 📋");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 3) Manager Approved => Employee gets to know
    @Async
    public void sendManagerApprovedEmail(String employeeEmail, String employeeName, String managerName) {
        if (employeeEmail == null) return;

        try {
            Context context = new Context();
            context.setVariable("employeeName", employeeName);
            context.setVariable("managerName", managerName);
            String htmlContent = templateEngine.process("manager-approved", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employeeEmail);
            helper.setSubject("Update: Your Appraisal Review is Complete 🎉");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
