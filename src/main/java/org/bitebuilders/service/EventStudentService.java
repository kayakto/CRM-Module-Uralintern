package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.model.EventStudentInfo;
import org.bitebuilders.repository.EventStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventStudentService {

    @Autowired
    private final EventStudentRepository eventStudentRepository;

    public EventStudentService(EventStudentRepository eventStudentRepository) {
        this.eventStudentRepository = eventStudentRepository;
    }

    // Метод, который возвращает всех студентов, которые отправили персональные данные для участия
    public List<EventStudentInfo> getEventStudents(Long eventId) {
        return eventStudentRepository.findByEventId(eventId); // Фильтрация по статусу
    }

    // Метод, который меняет статус студента
    public Boolean updateStudentStatus(Long eventId, Long studentId, StatusRequest newStatus) {
        EventStudent eventStudent = eventStudentRepository.findStudentEvent(studentId, eventId);
        eventStudent.setStudentStatus(newStatus);
        EventStudent savedEventStudent = eventStudentRepository.save(eventStudent);
        return savedEventStudent.getStudentStatus() == newStatus;
    }
}
