package org.bitebuilders.controller;

import org.bitebuilders.model.Message;
import org.bitebuilders.model.NotificationInfo;
import org.bitebuilders.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationInfo>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationInfo> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

//    @PostMapping("/send") // на случай, если отдельным запросом отправлять уведомления
    public ResponseEntity<String> sendNotification(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestParam Message.MessageStatus messageStatus) {
        notificationService.sendNotification(userId, eventId, messageStatus);
        return ResponseEntity.ok("Notification sent successfully");
    }

}

