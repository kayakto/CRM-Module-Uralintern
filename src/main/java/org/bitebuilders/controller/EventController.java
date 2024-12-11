package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventDTO;
import org.bitebuilders.controller.requests.EventRequest;
import org.bitebuilders.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bitebuilders.service.EventService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getOpenedEvents() {
        List<EventDTO> activeEvents = eventService.getOpenedEvents()
                .stream()
                .map(Event::toEventDTO)
                .toList();
        if (activeEvents != null)
            return ResponseEntity.ok(activeEvents);  // Возвращаем список активных событий
        return ResponseEntity.noContent().build();
    }

    // 3. Получение всех мероприятий по adminId
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<EventDTO>> getEventsByAdminId(@PathVariable Long adminId) {
        List<EventDTO> eventDTOS = eventService.getEventsByAdminId(adminId)
                .stream()
                .map(Event::toEventDTO)
                .toList();
        if (eventDTOS != null)
            return ResponseEntity.ok(eventDTOS);  // Возвращаем список событий для конкретного adminId
        return ResponseEntity.noContent().build();
    }

    // Получение мероприятия по его id
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventByID(@PathVariable Long eventId) {
        Optional<Event> foundEvent = eventService.getEventById(eventId);
        return foundEvent
                .map(event -> ResponseEntity.ok(event.toEventDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Получение статуса мероприятия по его id
    @GetMapping("/condition/{eventId}")
    public ResponseEntity<Event.Condition> getEventConditionByID(@PathVariable Long eventId) {
        Event.Condition result = eventService.getEventCondition(eventId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/post")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventRequest requestedEvent) {
        Event newEvent = requestedEvent.toEvent();
        EventDTO eventDTO = eventService.createOrUpdateEvent(newEvent).toEventDTO();
        if (eventDTO != null)
            return ResponseEntity.ok(eventDTO);
        return ResponseEntity.notFound().build();
    }

    /**
     * Запускает мероприятие досрочно
     */
    @PostMapping("/start-event")
    public ResponseEntity<EventDTO> startEvent(@RequestBody Long eventId) {
        Event startedEvent;
        try {
            startedEvent = eventService.startEventById(eventId);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(startedEvent.toEventDTO());
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId,
                                             @RequestBody EventRequest requestedEvent) {
        Event eventToUpdate = requestedEvent.toEvent(eventId);
        EventDTO eventDTO = eventService.createOrUpdateEvent(eventToUpdate).toEventDTO();
        if (eventDTO != null)
            return ResponseEntity.ok(eventDTO);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/hide/{eventId}")
    public ResponseEntity<Boolean> hideEvent(@PathVariable Long eventId) {
        Boolean result = eventService.hideEvent(eventId);
        return result ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Boolean> deleteEventByID(@PathVariable Long eventId) {
        Boolean result = eventService.deleteEvent(eventId);
        return result ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }
}
