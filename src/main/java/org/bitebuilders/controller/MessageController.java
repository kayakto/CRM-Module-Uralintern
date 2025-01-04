package org.bitebuilders.controller;

import org.bitebuilders.controller.requests.MessageUpdateRequest;
import org.bitebuilders.model.Message;
import org.bitebuilders.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{messageId}")
    public Message getMessageById(@PathVariable Long messageId) {
        return messageService.getMessageById(messageId);
    }

    @GetMapping("/event/{eventId}")
    public List<Message> getEventMessages(@PathVariable Long eventId) {
        return messageService.getEventMessages(eventId);
    }

    @PutMapping("/update-message")
    public Message updateMessage(@RequestBody MessageUpdateRequest messageUpdateRequest) {
        return messageService.updateMessage(
                messageUpdateRequest.getMessageId(), messageUpdateRequest.getText());
    }
}
