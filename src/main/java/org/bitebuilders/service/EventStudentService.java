package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.exception.UserNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.repository.EventGroupRepository;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.EventStudentRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.bitebuilders.model.Message.parseStatusRequestToMessageStatus;

@Service
public class EventStudentService {

    private final EventStudentRepository eventStudentRepository;

    private final UserInfoRepository userInfoRepository;

    private final EventRepository eventRepository;

    private final EventGroupRepository eventGroupRepository;

    private final NotificationService notificationService;

    @Autowired
    public EventStudentService(EventStudentRepository eventStudentRepository, UserInfoRepository userInfoRepository, EventCuratorService eventCuratorService, EventRepository eventRepository, EventGroupRepository eventGroupRepository, NotificationService notificationService) {
        this.eventStudentRepository = eventStudentRepository;
        this.userInfoRepository = userInfoRepository;
        this.eventRepository = eventRepository;
        this.eventGroupRepository = eventGroupRepository;
        this.notificationService = notificationService;
    }

    /**
     * Метод, который возвращает всех студентов,
     * которые отправили персональные данные для участия
      */
    public List<EventStudentInfo> getEventStudents(Long eventId) {
        return eventStudentRepository.findByEventId(eventId); // Фильтрация по статусу
    }

    public EventStudent getEventStudent(Long eventId, Long studentId) {
        Optional<EventStudent> eventStudent = eventStudentRepository.findStudentEvent(studentId, eventId);
        return eventStudent.orElseThrow(() -> new EventUserNotFoundException("Student does not exist on this event"));
    }

    public boolean isAllowedStatus(Long eventId, Long studentId) {
        EventStudent eventStudent = getEventStudent(eventId, studentId);
        StatusRequest current = eventStudent.getStudentStatus();
        return current == StatusRequest.SENT_PERSONAL_INFO || current == StatusRequest.TEST_PASSED;
        // IllegalArgumentException может лучше выкинуть
    }

    /**
     * Метод возвращающий список всех заявок студентов на мероприятие.
     * Если в мероприятии нет теста, то все студенты, которые отправили заявки.
     * Если есть тест, то только те студенты, которые прошли тест.
     */
    public List<EventStudentInfo> getWaitingStudentInfo(Long eventId) {
        if (eventRepository.
                findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event doesn`t exist"))
                .isHasTest())
            return eventStudentRepository.findPassedStudentsInfo(eventId);
        return eventStudentRepository.findSentStudentsInfo(eventId);
    }

    public List<EventStudentInfo> getAcceptedStudentInfo(Long eventId) {
        return eventStudentRepository.findAcceptedStudentsInfo(eventId);
    }

    public List<EventStudentInfo> getStartedStudentInfo(Long eventId) {
        return eventStudentRepository.findStartedEventStudentInfo(eventId);
    }

    public List<EventStudent> getAcceptedEventStudent(Long eventId) {
        return eventStudentRepository.findAcceptedEventStudent(eventId);
    }

    public List<EventStudent> getStartedEventStudent(Long eventId) {
        return eventStudentRepository.findStartedEventStudent(eventId);
    }

    public StatusRequest getStudentStatus(Long eventId, Long studentId) {
        return eventStudentRepository.findStudentEventStatus(studentId, eventId); // todo throw exception
    }

    public boolean canSend(Long eventId, Long studentId) {
        if (userInfoRepository.findById(studentId).isEmpty()) {
            throw new UserNotFoundException("student " + studentId + " not found");
        }
        else if (eventRepository.findById(eventId).isEmpty()) {
            throw new EventNotFoundException("event " + eventId + " not found");
        }
        return eventStudentRepository.findStudentEvent(studentId, eventId).isEmpty();
    }

    public EventStudent save(EventStudent eventStudent) {
        return eventStudentRepository.save(eventStudent);
    }

    /**
     * Метод, который меняет статус студента
     */
    @Transactional
    public boolean updateStudentStatus(Long eventId, Long studentId, StatusRequest newStatus) {
        Optional<EventStudent> optionalEventStudent = eventStudentRepository.findStudentEvent(studentId, eventId);
        EventStudent eventStudent;

        if (optionalEventStudent.isPresent()) {
            eventStudent = optionalEventStudent.get();
            if (newStatus == StatusRequest.SENT_PERSONAL_INFO) return false;
        } else {
            if (newStatus == StatusRequest.SENT_PERSONAL_INFO &&
                    userInfoRepository.findById(studentId).isPresent()) {
                eventStudent = new EventStudent(studentId, eventId, newStatus);
            } else {
                throw new EventUserNotFoundException(
                        "EventStudent not found for eventId: " + eventId + " and studentId: " + studentId
                );
            }
        } // TODO переделать так, чтобы были методы на проверку существования

        eventStudent.setStudentStatus(newStatus);
        EventStudent savedEventStudent = eventStudentRepository.save(eventStudent);

        // Отправляем уведомление
        if (newStatus == StatusRequest.ADDED_IN_CHAT
                || newStatus == StatusRequest.REJECTED_FROM_EVENT
                || newStatus == StatusRequest.SENT_PERSONAL_INFO
        ) {
            Message.MessageStatus messageStatus = parseStatusRequestToMessageStatus(newStatus);
            notificationService.sendNotification(studentId, eventId, messageStatus);
        } // TODO сделать уведомления для всех статусов

        return savedEventStudent.getStudentStatus() == newStatus;
    }

    @Transactional
    public void changeCurator(Long eventId, Long studentId, Long newCuratorId) {
        // Проверка наличия студента
        EventStudent student = eventStudentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException("Student not found with ID: " + studentId));

        // Проверка, что студент принадлежит мероприятию
        if (!student.getEventId().equals(eventId)) {
            throw new EventUserNotFoundException("Student does not belong to event with ID: " + eventId);
        }

        // Проверка наличия группы для нового куратора
        EventGroup newGroup = eventGroupRepository.findByEventIdAndCuratorId(eventId, newCuratorId);

        // Обновление groupId у студента
        student.setGroupId(newGroup.getId());
        eventStudentRepository.save(student); // Сохраняем изменения в базе
    }

    public List<Event> getStudentEvents(Long studentId) {
        return eventStudentRepository.findAcceptedEventsByStudent(studentId);
    }

    public List<EventStudentInfo> getCuratorGroup(Long eventId, Long curatorId) {
        return eventStudentRepository.findByEventIdAndCuratorId(eventId, curatorId);
    }
}
