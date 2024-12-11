package org.bitebuilders.service.schedule;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventGroup;
import org.bitebuilders.service.EventGroupService;
import org.bitebuilders.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EventGroupCreationService {

    @Autowired
    private final EventService eventService;

    @Autowired
    private final EventGroupService eventGroupService;

    public EventGroupCreationService(EventService eventService, EventGroupService eventGroupService) {
        this.eventService = eventService;
        this.eventGroupService = eventGroupService;
    }

    @Scheduled(fixedRate = 60000) // Проверка каждые 60 секунд
    public void startEvents() {
        List<Event> events;
        events = eventService.getEventsMoreEnrollmentStartDate(OffsetDateTime.now());

        for (Event event : events) {
            List<EventGroup> groups = eventGroupService.createGroups(event.getId());
            System.out.println("Groups: " + groups);

            event.setCondition(Event.Condition.IN_PROGRESS);
            System.out.println("Groups created for event: " + event.getId());
            eventService.createOrUpdateEvent(event);
        }
    }

    public Event startEventById(Long eventId) {
        // Получение мероприятия по ID
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }

        Event eventToStart = eventOptional.get();

        // Разрешённые статусы для старта мероприятия
        Event.Condition[] canStart = new Event.Condition[] {
                Event.Condition.REGISTRATION_OPEN,
                Event.Condition.NO_SEATS,
                Event.Condition.REGISTRATION_CLOSED
        };

        // Проверка, может ли мероприятие быть запущено
        if (!Arrays.asList(canStart).contains(eventToStart.getCondition())) {
            throw new IllegalStateException("Event cannot be started due to its current condition: " + eventToStart.getCondition());
        }

        // Создание групп для мероприятия
        List<EventGroup> groups = eventGroupService.createGroups(eventToStart.getId());
        System.out.println("Groups created for event ID " + eventToStart.getId() + ": " + groups);

        // Установка статуса мероприятия в "IN_PROGRESS"
        eventToStart.setCondition(Event.Condition.IN_PROGRESS);
        System.out.println("Event with ID " + eventToStart.getId() + " is now in progress.");

        // Сохранение изменений в мероприятии
        return eventService.createOrUpdateEvent(eventToStart);
    }
}

