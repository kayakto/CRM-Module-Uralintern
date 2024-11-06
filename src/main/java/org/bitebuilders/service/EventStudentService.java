package org.bitebuilders.service;

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

    // TODO    EventStudentInfo deleteFromEvent() // find - update status - save
}
