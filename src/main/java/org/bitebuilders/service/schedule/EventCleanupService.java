package org.bitebuilders.service;

import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EventCleanupService {

    private final EventRepository eventRepository;

    public EventCleanupService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Запуск каждый день в полночь
    public void cleanUpDeletedEvents() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        eventRepository.deleteByConditionAndUpdatedBefore(Event.Condition.DELETED, thirtyDaysAgo);
    }
}
