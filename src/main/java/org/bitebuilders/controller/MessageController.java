package org.bitebuilders.controller;

import org.bitebuilders.controller.requests.MessageUpdateRequest;
import org.bitebuilders.model.Message;
import org.bitebuilders.service.EventService;
import org.bitebuilders.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class MessageController {

    private final MessageService messageService;

    private final EventService eventService;

    @Autowired
    public MessageController(MessageService messageService, EventService eventService) {
        this.messageService = messageService;
        this.eventService = eventService;
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long messageId) {
        Message message = messageService.getMessageById(messageId);

        if (!eventService.haveManagerAdminAccess(message.getEventId()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(message);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Message>> getEventMessages(@PathVariable Long eventId) {
        if (!eventService.haveManagerAdminAccess(eventId))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(messageService.getEventMessages(eventId));
    }

    @PutMapping("/update-message")
    public ResponseEntity<Message> updateMessage(@RequestBody MessageUpdateRequest messageUpdateRequest) {
        Message oldMessage = messageService.getMessageById(messageUpdateRequest.getMessageId());

        if (!eventService.haveManagerAdminAccess(oldMessage.getEventId()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                messageService.updateMessage(
                        oldMessage, messageUpdateRequest.getText())
        );
    }
}
