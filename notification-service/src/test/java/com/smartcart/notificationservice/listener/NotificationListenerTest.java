package com.smartcart.notificationservice.listener;

import com.smartcart.notificationservice.dto.NotificationEvent;
import com.smartcart.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void consumeMessage_ShouldCallService() {
        NotificationEvent event = new NotificationEvent(1L, "Title", "Message", "INFO");

        notificationListener.consumeMessage(event);

        verify(notificationService, times(1)).sendNotification(1L, "Title", "Message", "INFO");
    }
}
