package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.model.EventStudentInfo;
import org.bitebuilders.repository.EventStudentRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventStudentService {

    @Autowired
    private final EventStudentRepository eventStudentRepository;

    @Autowired
    private final UserInfoRepository userInfoRepository;

    public EventStudentService(EventStudentRepository eventStudentRepository, UserInfoRepository userInfoRepository) {
        this.eventStudentRepository = eventStudentRepository;
        this.userInfoRepository = userInfoRepository;
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
        return eventStudent.orElse(null);
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
        }

        eventStudent.setStudentStatus(newStatus);
        EventStudent savedEventStudent = eventStudentRepository.save(eventStudent);

        return savedEventStudent.getStudentStatus() == newStatus;
    }

    /**
     * Метод возвращающий список всех заявок студентов на мероприятие.
     */
    public List<EventStudentInfo> getSentStudentInfo(Long eventId) {
        return eventStudentRepository.findWaitingStudents(eventId);
    }
}
