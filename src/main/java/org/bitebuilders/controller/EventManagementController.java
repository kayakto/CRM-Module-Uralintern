package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventDTO;
import org.bitebuilders.model.Event;
import org.bitebuilders.service.schedule.EventGroupCreationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events-management")
public class EventManagementController {

    private final EventGroupCreationService eventGroupCreationService;

    public EventManagementController(EventGroupCreationService eventGroupCreationService) {
        this.eventGroupCreationService = eventGroupCreationService;
    }

    /**
     * Запускает мероприятие досрочно
     */
    @PostMapping("/start-event")
    public ResponseEntity<EventDTO> startEvent(@RequestBody Long eventId) {
        Event startedEvent;
        try {
            startedEvent = eventGroupCreationService.startEventById(eventId);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(startedEvent.toEventDTO());
    }
}

