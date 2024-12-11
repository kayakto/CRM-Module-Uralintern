package org.bitebuilders.service;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.service.schedule.EventGroupCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

//    @Autowired
//    private final EventGroupCreationService eventGroupCreationService;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void isPresentEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new EventNotFoundException("Event doesn`t exist");
    }

    // Метод, который возвращает все мероприятия со статусом регистрация открыта
    public List<Event> getOpenedEvents() {
        return eventRepository.findAllByCondition(Event.Condition.REGISTRATION_OPEN); // Используем метод репозитория
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

    // Получение статуса мероприятия GET - возвращает текущий статус мероприятия, на основе его параметров (даты, количество мест).
    public Event.Condition getEventCondition(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Event not found");
        }
        return eventOptional.get().getCondition();
    }

    // Метод, который сохраняет Event и возвращает его
    @Transactional
    public Event createOrUpdateEvent(Event event) {
        // Сохраняем объект в базе данных
        return eventRepository.save(event); // Возвращаем его после сохранения
    }

    public void deleteAllEvents() {
        eventRepository.deleteAll();
    }

    // Ручное управление статусом (для администратора) - администратор вручную изменяет статус мероприятия (“Скрыто”, “Удалено“)
    @Transactional
    public Boolean deleteEvent(Long eventId) {
        Optional<Event> eventToDelete = getEventById(eventId);
        return eventToDelete.map(event -> {
            event.setCondition(Event.Condition.DELETED);
            eventRepository.save(event); // Сохраняем изменение статуса в базе данных
            return true;
        }).orElse(false);
    }

    @Transactional
    public Boolean hideEvent(Long eventId) {
        Optional<Event> eventToHide = getEventById(eventId);
        return eventToHide.map(event -> {
            event.setCondition(Event.Condition.HIDDEN);
            eventRepository.save(event); // Сохраняем изменение статуса в базе данных
            return true;
        }).orElse(false);
    }

    @Scheduled(fixedRate = 60000) // Обновляем статусы каждую минуту
    @Transactional
    public void updateEventStatuses() {
        List<Event> events = getAllEvents();

        for (Event event : events) {
            Event.Condition newCondition = calculateCondition(event);
            if (newCondition != event.getCondition()) {
                event.setCondition(newCondition);
                createOrUpdateEvent(event);
            }
        }
    } // TODO вынести в отдельный класс

    private Event.Condition calculateCondition(Event event) {
        OffsetDateTime now = OffsetDateTime.now();

        if (now.isBefore(event.getEnrollmentStartDate())) {
            return Event.Condition.PREPARATION;
        } else if (now.isAfter(event.getEnrollmentStartDate()) && now.isBefore(event.getEnrollmentEndDate())) {
            if (event.getNumberSeatsStudent() > 0) {
                return Event.Condition.REGISTRATION_OPEN;
            } else {
                return Event.Condition.NO_SEATS;
            }
        } else if (now.isAfter(event.getEnrollmentEndDate()) && now.isBefore(event.getEventStartDate())) {
            return Event.Condition.REGISTRATION_CLOSED;
        } else if (now.isAfter(event.getEventStartDate()) && now.isBefore(event.getEventEndDate())) {
//            Event startedEvent = eventGroupCreationService.startEventById(event.getId()); // может в методе выше это сделать
//            return startedEvent.getCondition();
            return Event.Condition.IN_PROGRESS;
        } else if (now.isAfter(event.getEventEndDate())) {
            return Event.Condition.FINISHED; // TODO логика завершения
        }

        return event.getCondition(); // Не меняем статус для удалённых/скрытых мероприятий
    }
}
