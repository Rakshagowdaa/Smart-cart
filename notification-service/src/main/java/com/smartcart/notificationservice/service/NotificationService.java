package com.smartcart.notificationservice.service;

import com.smartcart.notificationservice.client.UserClient;
import com.smartcart.notificationservice.entity.Notification;
import com.smartcart.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final UserClient userClient;

    @Async
    public void sendNotification(Long userId, String title, String message, String type) {
        System.out.println("---- Saving " + type + " to User " + userId + " ----");
        
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(new Date())
                .build();

        notificationRepository.save(notification);

        try {
            UserClient.UserDto user = userClient.getUserById(userId);
            if (user != null && user.getEmail() != null) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject(title);
                mailMessage.setText(message);
                
                mailSender.send(mailMessage);
                System.out.println("Email sent successfully to " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Failed to send email to user " + userId + ": " + e.getMessage());
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
