package org.bitebuilders.service;

import org.bitebuilders.component.UserContext;
import org.bitebuilders.enums.UserRole;
import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventGroup;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class EventService {

    /**
     *  репозиторий с готовыми методами для events
     */
    private final EventRepository eventRepository;

    private final EventGroupService eventGroupService;

    private final UserContext userContext;

    private final UserInfoService userInfoService;

    private final EventStudentService eventStudentService;

    private final EventCuratorService eventCuratorService;

    @Autowired
    public EventService(EventRepository eventRepository, EventGroupService eventGroupService, UserContext userContext, UserInfoService userInfoService, EventStudentService eventStudentService, EventCuratorService eventCuratorService) {
        this.eventRepository = eventRepository;
        this.eventGroupService = eventGroupService;
        this.userContext = userContext;
        this.userInfoService = userInfoService;
        this.eventStudentService = eventStudentService;
        this.eventCuratorService = eventCuratorService;
    }

    public void isPresentEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new EventNotFoundException("Event doesn`t exist");
    }

    // Метод, который возвращает все мероприятия со статусом регистрация открыта
    public List<Event> getOpenedEvents() {
        return eventRepository.findAllByCondition(Event.Condition.REGISTRATION_OPEN);
    }

    // Метод, который возвращает все мероприятия
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    // Метод, который возвращает все мероприятия, у которых есть переданный admin_id
    public List<Event> getEventsByAdminId(Long adminId) {
        return eventRepository.findAllByAdminId(adminId);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    public List<Event> getEventsMoreEventStartDate(OffsetDateTime dateTime) {
        return eventRepository.findStartedEventsByDate(dateTime);
    }

    public List<Event> getMyEvents() {
        UserInfo user = userContext.getCurrentUser();
        Long userId = user.getId();

        switch (user.getRole_enum()) {
            case ADMIN -> {
                return eventRepository.findAllByAdminId(userId);
            }
            case MANAGER -> {
                return eventRepository.findAllByManagerId(userId);
            }
            case CURATOR -> {
                return eventCuratorService.getCuratorEvents(userId);
            }
            case STUDENT -> {
                return eventStudentService.getStudentEvents(userId);
            }
        }

        return Collections.emptyList();
    }

    // Метод, который сохраняет Event и возвращает его
    @Transactional
    public Event createOrUpdateEvent(Event event) {
        // случай для обновления или создания мероприятия через контроллер
        if (event.getCondition() == null) {
            validateEvent(event);
            return updateEventCondition(event);
        } else
            return eventRepository.save(event);
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
            return updateEventCondition(eventToHide).getCondition();
        }

        eventToHide.setCondition(Event.Condition.HIDDEN);
        return eventRepository.save(eventToHide).getCondition();
    }

    public Event updateEventCondition(Event event) {
        Event.Condition newCondition = calculateCondition(event);
        Event.Condition currentCondition = event.getCondition();

        if (newCondition != currentCondition) {
            if (newCondition == Event.Condition.IN_PROGRESS) {
                return startEventById(event.getId());
            } else {
                event.setCondition(newCondition);
                return createOrUpdateEvent(event);
            }
        }

        return event;
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

        eventToStart.setCondition(Event.Condition.IN_PROGRESS);
        System.out.println("Event with ID " + eventToStart.getId() + " is now in progress.");

        // Сохранение изменений в мероприятии
        return createOrUpdateEvent(eventToStart);
    }

    private Event getEventToStart(Long eventId) {
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

    public boolean validateEvent(Event event) {
        if (!userInfoService.isAdmin(event.getAdminId())) {
            throw new IllegalArgumentException("Invalid adminId: Administrator does not exist.");
        }
        if (!userInfoService.isManager(event.getManagerId())) {
            throw new IllegalArgumentException("Invalid managerId: Manager does not exist.");
        }

        if (event.getEventStartDate().isAfter(event.getEventEndDate())
                || event.getEventStartDate().isEqual(event.getEventEndDate())) {
            throw new IllegalArgumentException("Event start date must be before event end date.");
        }
        if (event.getEnrollmentStartDate().isAfter(event.getEnrollmentEndDate())
                || event.getEnrollmentStartDate().isEqual(event.getEnrollmentEndDate())) {
            throw new IllegalArgumentException("Enrollment start date must be before enrollment end date.");
        }
        if (event.getEnrollmentEndDate().isAfter(event.getEventStartDate())) {
            throw new IllegalArgumentException("Enrollment end date must be before event start date.");
        }

        if (event.getNumberSeatsStudent() <= 0) {
            throw new IllegalArgumentException("Number of seats for students must be greater than zero.");
        }

        if (event.getChatUrl() == null || !event.getChatUrl().startsWith("http") || !event.getChatUrl().contains(".")) {
            throw new IllegalArgumentException("Invalid chat URL.");
        }

        return true;
    }
}
