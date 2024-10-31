package org.bitebuilders.service;

import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.EventStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    /**
     * репозиторий с готовыми методами для events
     */
    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final EventStudentRepository eventStudentRepository;

    public EventService(EventRepository eventRepository, EventStudentRepository eventStudentRepository) {
        this.eventRepository = eventRepository;
        this.eventStudentRepository = eventStudentRepository;
    }

    // Метод, который возвращает все мероприятия со статусом ACTIVE
    public List<Event> getActiveEvents() {
        return eventRepository.findAllByCondition(Event.Condition.ACTIVE); // Используем метод репозитория
    }

    // Метод, который возвращает все мероприятия, у которых есть переданный admin_id
    public List<Event> getEventsByAdminId(Long adminId) {
        return eventRepository.findAllByAdminId(adminId); // Фильтрация по admin_id
    }

    // Метод, который возвращает всех студентов, которые отправили персональные данные для участия
    public List<EventStudent> getEventStudents(Long eventId) {
        return eventStudentRepository.findByEventId(eventId); // Фильтрация по статусу
    }

    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    // Метод, который сохраняет Event и возвращает его
    @Transactional
    public Event createOrUpdateEvent(Event event) {
        // Сохраняем объект в базе данных
        return eventRepository.save(event); // Возвращаем его после сохранения
    }

    public Boolean deleteEvent(Long eventId) {
        Optional<Event> eventToDelete = getEventById(eventId);
        return eventToDelete.map(event -> {
            event.setCondition(Event.Condition.DELETED);
            eventRepository.save(event); // Сохраняем изменение статуса в базе данных
            return true;
        }).orElse(false);
    }
}
