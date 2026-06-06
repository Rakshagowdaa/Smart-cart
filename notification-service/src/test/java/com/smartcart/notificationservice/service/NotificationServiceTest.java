package com.smartcart.notificationservice.service;

import com.smartcart.notificationservice.client.UserClient;
import com.smartcart.notificationservice.entity.Notification;
import com.smartcart.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_WithEmail() {
        UserClient.UserDto user = new UserClient.UserDto();
        user.setEmail("test@example.com");
        when(userClient.getUserById(1L)).thenReturn(user);

        notificationService.sendNotification(1L, "Title", "Msg", "INFO");

        verify(notificationRepository).save(any(Notification.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_EmailFailure_ShouldNotThrow() {
        UserClient.UserDto user = new UserClient.UserDto();
        user.setEmail("test@example.com");
        when(userClient.getUserById(1L)).thenReturn(user);
        doThrow(new RuntimeException("SMTP Error")).when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.sendNotification(1L, "Title", "Msg", "INFO");

        verify(notificationRepository).save(any(Notification.class));
        // Success even if email fails
    }

    @Test
    void getNotificationsForUser() {
        Notification notification = Notification.builder().id(1L).title("Title").build();
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(notification));

        List<Notification> result = notificationService.getNotificationsForUser(1L);

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void markAsRead() {
        Notification notification = Notification.builder().id(1L).isRead(false).build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }
}
