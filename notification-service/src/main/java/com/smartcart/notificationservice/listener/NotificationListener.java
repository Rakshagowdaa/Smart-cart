package com.smartcart.notificationservice.listener;

import com.smartcart.notificationservice.dto.NotificationEvent;
import com.smartcart.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consumeMessage(NotificationEvent event) {
        notificationService.sendNotification(
            event.getUserId(),
            event.getTitle(),
            event.getMessage(),
            event.getType()
        );
    }
}
