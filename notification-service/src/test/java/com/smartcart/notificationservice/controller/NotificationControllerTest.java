package com.smartcart.notificationservice.controller;

import com.smartcart.notificationservice.entity.Notification;
import com.smartcart.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void triggerNotification() throws Exception {
        mockMvc.perform(post("/api/notifications/send")
                .param("userId", "1")
                .param("title", "Test Title")
                .param("message", "Test Msg")
                .param("type", "INFO"))
                .andExpect(status().isAccepted());

        verify(notificationService).sendNotification(1L, "Test Title", "Test Msg", "INFO");
    }

    @Test
    void getUserNotifications() throws Exception {
        Notification notification = Notification.builder().id(1L).title("Test Title").build();
        when(notificationService.getNotificationsForUser(1L)).thenReturn(Arrays.asList(notification));

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    void markAsRead() throws Exception {
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L);
    }
}
