package org.bitebuilders.service.schedule;

import org.bitebuilders.model.Event;
import org.bitebuilders.service.EventGroupService;
import org.bitebuilders.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Scheduled(fixedRate = 60000) // Проверка каждые 60 секунд
    public void startEvents() {
        List<Event> events = eventService.getEventsByEnrollmentStartDate(OffsetDateTime.now());
        for (Event event : events) {
            try {
                eventGroupService.createGroup(event.getId());
                event.setCondition(Event.Condition.STARTED); // TODO мб поменяю названию
                System.out.println("Groups created for event: " + event.getId());
                eventService.save(event);
            } catch (Exception e) {
                System.err.println("Error creating groups for event: " + event.getId());
            }
        }
    }
}

