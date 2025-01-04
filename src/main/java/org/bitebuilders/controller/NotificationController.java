package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.MessageResponseDTO;
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

    @GetMapping("/my")
    public ResponseEntity<List<NotificationInfo>> getUserNotifications() {
        List<NotificationInfo> notifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(notifications);
    }

//    @PostMapping("/send") // на случай, если отдельным запросом отправлять уведомления
    public ResponseEntity<MessageResponseDTO> sendNotification(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestParam Message.MessageStatus messageStatus) {
        notificationService.sendNotification(userId, eventId, messageStatus);
        return ResponseEntity.ok(
                new MessageResponseDTO("Notification sent successfully")
        );
    }

}

