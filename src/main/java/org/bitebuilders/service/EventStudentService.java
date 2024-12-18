package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.repository.EventGroupRepository;
import org.bitebuilders.repository.EventStudentRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.bitebuilders.model.Message.parseStatusRequestToMessageStatus;

@Service
public class EventStudentService {

    @Autowired
    private final EventStudentRepository eventStudentRepository;

    @Autowired
    private final UserInfoRepository userInfoRepository;

    @Autowired
    private final EventGroupRepository eventGroupRepository;

    @Autowired
    private final NotificationService notificationService;

    public EventStudentService(EventStudentRepository eventStudentRepository, UserInfoRepository userInfoRepository, EventCuratorService eventCuratorService, EventGroupRepository eventGroupRepository, NotificationService notificationService) {
        this.eventStudentRepository = eventStudentRepository;
        this.userInfoRepository = userInfoRepository;
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


    /**
     * Метод возвращающий список всех заявок студентов на мероприятие.
     */
    public List<EventStudentInfo> getSentStudentInfo(Long eventId) {
        return eventStudentRepository.findWaitingStudentsInfo(eventId);
    }

    public List<EventStudent> getAcceptedEventStudent(Long eventId) {
        return eventStudentRepository.findAcceptedEventStudent(eventId);
    }

    public StatusRequest getStudentStatus(Long eventId, Long studentId) {
        return eventStudentRepository.findStudentEventStatus(studentId, eventId); // todo throw exception
    }

    // мб транзактионал
    public EventStudent save(EventStudent eventStudent) {
        return eventStudentRepository.save(eventStudent);
    }

    /**
     * Метод, который меняет статус студента
     */
    @Transactional
    public Boolean updateStudentStatus(Long eventId, Long studentId, StatusRequest newStatus) {
        Optional<EventStudent> optionalEventStudent = eventStudentRepository.findStudentEvent(studentId, eventId);
        EventStudent eventStudent;
        if (optionalEventStudent.isPresent()) {
            eventStudent = optionalEventStudent.get();
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
        if (newStatus == StatusRequest.ADDED_IN_CHAT || newStatus == StatusRequest.REJECTED_FROM_EVENT) {
            Message.MessageStatus messageStatus = parseStatusRequestToMessageStatus(newStatus);
            notificationService.sendNotification(studentId, eventId, messageStatus);
        }

        return savedEventStudent.getStudentStatus() == newStatus;
    }

    @Transactional
    public void changeCurator(Long eventId, Long studentId, Long newCuratorId) {
        // Проверка наличия студента
        EventStudent student = eventStudentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Проверка, что студент принадлежит мероприятию
        if (!student.getEventId().equals(eventId)) {
            throw new IllegalArgumentException("Student does not belong to event with ID: " + eventId);
        }

        // Проверка наличия группы для нового куратора
        EventGroup newGroup = eventGroupRepository.findByEventIdAndCuratorId(eventId, newCuratorId);

        // Обновление groupId у студента
        student.setGroupId(newGroup.getId());
        eventStudentRepository.save(student); // Сохраняем изменения в базе
    }
}
