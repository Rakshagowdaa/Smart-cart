package com.smartcart.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestParam("userId") Long userId,
                          @RequestParam("title") String title,
                          @RequestParam("message") String message,
                          @RequestParam("type") String type);
}
