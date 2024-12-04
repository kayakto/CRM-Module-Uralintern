package org.bitebuilders.controller;

import org.bitebuilders.model.Event;
import org.bitebuilders.service.EventGroupService;
import org.bitebuilders.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/events-management")
public class EventManagementController {

    private final EventService eventService;
    private final EventGroupService eventGroupService;

    public EventManagementController(EventService eventService, EventGroupService eventGroupService) {
        this.eventService = eventService;
        this.eventGroupService = eventGroupService;
    }

    /**
     * Запуск создания групп для мероприятий с текущей датой начала регистрации.
     */
    @PostMapping("/start")
    public ResponseEntity<String> manuallyStartEvents() {
        List<Event> events = eventService.getEventsByEnrollmentStartDate(OffsetDateTime.now());
        if (events.isEmpty()) {
            return ResponseEntity.ok("No events found to start.");
        }

        StringBuilder result = new StringBuilder();
        for (Event event : events) {
            try {
                eventGroupService.createGroup(event.getId());
                event.setCondition(Event.Condition.STARTED);
                eventService.save(event);

                result.append("Groups created for event ID: ").append(event.getId()).append("\n");
            } catch (Exception e) {
                result.append("Error creating groups for event ID: ").append(event.getId())
                        .append(". Error: ").append(e.getMessage()).append("\n");
            }
        }

        return ResponseEntity.ok(result.toString());
    } // TODO потестить еще
}

