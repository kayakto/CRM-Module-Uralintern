package org.bitebuilders.service;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    /**
     * репозиторий с готовыми методами для events
     */
    @Autowired
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void isPresentEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new EventNotFoundException("Event doesn`t exist");
    }

    // Метод, который возвращает все мероприятия со статусом ACTIVE
    public List<Event> getActiveEvents() {
        return eventRepository.findAllByCondition(Event.Condition.ACTIVE); // Используем метод репозитория
    }

    // Метод, который возвращает все мероприятия
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll(); // Используем метод репозитория
    }

    // Метод, который возвращает все мероприятия, у которых есть переданный admin_id
    public List<Event> getEventsByAdminId(Long adminId) {
        return eventRepository.findAllByAdminId(adminId); // Фильтрация по admin_id
    }

    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> getEventsMoreEnrollmentStartDate(OffsetDateTime dateTime) {
        return eventRepository.findStartedEventsByDate(dateTime);
    }

    @Transactional
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    // Метод, который сохраняет Event и возвращает его
    @Transactional
    public Event createOrUpdateEvent(Event event) {
        // Сохраняем объект в базе данных
        return eventRepository.save(event); // Возвращаем его после сохранения
    }

    @Transactional
    public Boolean deleteEvent(Long eventId) {
        Optional<Event> eventToDelete = getEventById(eventId);
        return eventToDelete.map(event -> {
            event.setCondition(Event.Condition.DELETED);
            eventRepository.save(event); // Сохраняем изменение статуса в базе данных
            return true;
        }).orElse(false);
    }

    public void deleteAllEvents() {
        eventRepository.deleteAll();
    }
}
