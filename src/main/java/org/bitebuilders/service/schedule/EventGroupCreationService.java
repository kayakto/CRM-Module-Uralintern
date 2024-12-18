package org.bitebuilders.service.schedule;

import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventGroup;
import org.bitebuilders.service.EventGroupService;
import org.bitebuilders.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

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

    @Scheduled(fixedRate = 600000) // Проверка каждые 60 минут
    public void startEvents() {
        List<Event> events;
        events = eventService.getEventsMoreEventStartDate(OffsetDateTime.now());

        for (Event event : events) {
            List<EventGroup> groups = eventGroupService.createGroups(event.getId());
            System.out.println("Groups: " + groups);

            event.setCondition(Event.Condition.IN_PROGRESS);
            System.out.println("Groups created for event: " + event.getId());
            eventService.createOrUpdateEvent(event);
        }
    }
}

