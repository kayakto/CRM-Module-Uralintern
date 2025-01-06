package org.bitebuilders.service;

import org.bitebuilders.component.UserContext;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventGroup;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventService {
    /**
     *  репозиторий с готовыми методами для events
     */
    private final EventRepository eventRepository;

    private final EventGroupService eventGroupService;

    private final UserContext userContext;

    @Autowired
    public EventService(EventRepository eventRepository, EventGroupService eventGroupService, UserContext userContext) {
        this.eventRepository = eventRepository;
        this.eventGroupService = eventGroupService;
        this.userContext = userContext;
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

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    public List<Event> getEventsMoreEventStartDate(OffsetDateTime dateTime) {
        return eventRepository.findStartedEventsByDate(dateTime);
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
    public boolean deleteEvent(Long eventId) {
        Event eventToDelete = getEventById(eventId);
        eventToDelete.setCondition(Event.Condition.DELETED);
        eventRepository.save(eventToDelete);
        return true;
    }

    @Transactional
    public Event.Condition hideOrFindOutEvent(Long eventId) {
        Event eventToHide = getEventById(eventId);

        if (eventToHide.getCondition() == Event.Condition.HIDDEN){
            eventToHide.setCondition(Event.Condition.PREPARATION);
            updateEventCondition(eventToHide);
        }
        else eventToHide.setCondition(Event.Condition.HIDDEN);

        return eventRepository.save(eventToHide).getCondition();
    }

    @Scheduled(fixedRate = 600000) // Обновляем статусы каждый час
    @Transactional
    public void updateEventConditions() {
        List<Event> events = getAllEvents();

        for (Event event : events) {
            updateEventCondition(event);
        }
    } // TODO вынести в отдельный класс

    private void updateEventCondition(Event event) {
        Event.Condition newCondition = calculateCondition(event);
        Event.Condition currentCondition = event.getCondition();

        if (newCondition != currentCondition) {
            if (currentCondition == Event.Condition.IN_PROGRESS) {
                startEventById(event.getId());
            } else {
                event.setCondition(newCondition);
                createOrUpdateEvent(event);
            }
        }
    }

    private Event.Condition calculateCondition(Event event) {
        OffsetDateTime now = OffsetDateTime.now();
        Event.Condition currentCondition = event.getCondition();

        // Не меняем статус для удалённых/скрытых мероприятий
        if (currentCondition == Event.Condition.HIDDEN || currentCondition == Event.Condition.DELETED) {
            return currentCondition;
        }

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
            return Event.Condition.IN_PROGRESS;
        } else if (now.isAfter(event.getEventEndDate())) {
            return Event.Condition.FINISHED; // TODO логика завершения
        }

        return currentCondition;
    }

    public Event startEventById(Long eventId) {
        Event eventToStart = getEventToStart(eventId);

        // Создание групп для мероприятия
        List<EventGroup> groups = eventGroupService.createGroups(eventToStart.getId());
        System.out.println("Groups created for event ID " + eventToStart.getId() + ": " + groups);

        // Установка статуса мероприятия в "IN_PROGRESS"
        eventToStart.setCondition(Event.Condition.IN_PROGRESS);
        System.out.println("Event with ID " + eventToStart.getId() + " is now in progress.");

        // Сохранение изменений в мероприятии
        return createOrUpdateEvent(eventToStart);
    }

    private Event getEventToStart(Long eventId) {
        // Получение мероприятия по ID
        Event eventToStart = getEventById(eventId);
        Event.Condition currentCondition = eventToStart.getCondition();

        // Разрешённые статусы для старта мероприятия
        List<Event.Condition> canStart = Arrays.asList(
                Event.Condition.REGISTRATION_OPEN,
                Event.Condition.NO_SEATS,
                Event.Condition.REGISTRATION_CLOSED
        );

        // Проверка, может ли мероприятие быть запущено
        if (!canStart.contains(currentCondition)) {
            throw new IllegalStateException("Event cannot be started due to its current condition: " + currentCondition);
        }
        return eventToStart;
    }

    public boolean haveManagerAdminAccess(Long eventId) {
        UserInfo user = userContext.getCurrentUser();
        Event event = getEventById(eventId);

        switch (user.getRole_enum()){
            case UserRole.ADMIN -> {
                if (!Objects.equals(event.getAdminId(), user.getId())) {
                    throw new AccessDeniedException("This admin does not have permission to this event");
                }
            }
            case UserRole.MANAGER -> {
                if (!Objects.equals(event.getManagerId(), user.getId())) {
                    throw new AccessDeniedException("This manager does not have permission to this event");
                }
            }
        }

        return true;
    }

    public boolean haveAdminAccess(Long eventId) {
        UserInfo user = userContext.getCurrentUser();
        Event event = getEventById(eventId);
        if (!Objects.equals(event.getAdminId(), user.getId())) {
            throw new AccessDeniedException("This admin does not have permission to this event");
        }
        return true;
    }

    public boolean haveManagerAccess(Long eventId) {
        UserInfo user = userContext.getCurrentUser();
        Event event = getEventById(eventId);
        if (!Objects.equals(event.getManagerId(), user.getId())) {
            throw new AccessDeniedException("This manager does not have permission to this event");
        }
        return true;
    }
}
